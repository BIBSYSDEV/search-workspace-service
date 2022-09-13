package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.ApiMessageParser;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.GatewayResponseSerializingException;
import nva.commons.apigateway.exceptions.UnsupportedAcceptHeaderException;
import nva.commons.core.Environment;
import nva.commons.core.ioutils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.utils.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static nva.commons.core.attempt.Try.attempt;


public abstract class OpenSearchProxyHandler<I, O> extends ApiGatewayHandler<I, O>  {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchProxyHandler.class);
    private final transient ApiMessageParser<I> inputParser = new ApiMessageParser<>();

    public OpenSearchProxyHandler(Class<I> iclass) {
        this(iclass, new Environment());
    }

    public OpenSearchProxyHandler(Class<I> iclass, Environment environment) {
        this(iclass, environment, dtoObjectMapper);
    }

    public OpenSearchProxyHandler(Class<I> iclass, Environment environment, ObjectMapper objectMapper) {
        super(iclass, environment);
        this.objectMapper = objectMapper;
    }

    private String getSerializedOutput(O output) throws JsonProcessingException {
        return output instanceof String ? (String) output : objectMapper.writeValueAsString(output);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        logger.info(REQUEST_ID + context.getAwsRequestId());
        I inputObject = null;
        try {
            init(outputStream, context);
            String inputString = IoUtils.streamToString(inputStream);
            inputObject = attempt(() -> parseInput(inputString))
                    .orElseThrow(this::parsingExceptionToBadRequestException);

            RequestInfo requestInfo = inputParser.getRequestInfo(inputString);

            Pair<O, Integer> response = inputToOutputWithStatus(inputObject, requestInfo, context);

            writeOutputWithStatus(response, requestInfo);
        } catch (ApiGatewayException e) {
            handleExpectedException(context, inputObject, e);
        } catch (Exception e) {
            handleUnexpectedException(context, inputObject, e);
        }
    }

    protected void writeOutputWithStatus(Pair<O, Integer> output, RequestInfo requestInfo)
            throws IOException, GatewayResponseSerializingException, UnsupportedAcceptHeaderException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            Map<String, String> headers = getSuccessHeaders(requestInfo);
            Integer statusCode = output.right();
            String serializedOutput = getSerializedOutput(output.left());
            GatewayResponse<String> gatewayResponse = new GatewayResponse<>(serializedOutput, headers, statusCode,
                    objectMapper);
            String responseJson = objectMapper.writeValueAsString(gatewayResponse);
            writer.write(responseJson);
        }
    }


    @Override
    protected O processInput(I input, RequestInfo requestInfo, Context context) {
        throw new IllegalStateException("processInput never be called");
    }

    @Override
    protected Integer getSuccessStatusCode(I input, O output) {
        throw new IllegalStateException("getSuccessStatusCode should never be called");
    }

    abstract Pair<O, Integer> inputToOutputWithStatus(I input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException;
}
