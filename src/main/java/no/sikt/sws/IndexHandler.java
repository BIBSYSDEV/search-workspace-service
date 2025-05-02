package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import java.nio.charset.StandardCharsets;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.RequestTooLargeException;
import no.sikt.sws.models.opensearch.OpenSearchCommandKind;
import no.sikt.sws.models.opensearch.OpenSearchResponseKind;
import nva.commons.apigateway.ApiGatewayProxyHandler;
import nva.commons.apigateway.ProxyResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static com.amazonaws.http.HttpMethodName.POST;
import static com.amazonaws.http.HttpMethodName.PUT;
import static no.sikt.sws.constants.ApplicationConstants.REQUIRED_PARAMETER_IS_NULL;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class IndexHandler extends ApiGatewayProxyHandler<String, String> {

    public static final String RESOURCE_IDENTIFIER = "resource";

    public static final String INTERNAL_ERROR = "Internal error";
    public static final String TOO_LARGE_RESULTS = "Search response too large. Try to narrow down the search or use "
                                                   + "pagination";
    public static final int AWS_LAMBDA_RESPONSE_LIMIT_BYTES = 5 * 1024 * 1024;
    public OpenSearchClient openSearchClient = OpenSearchClient.passthroughClient();


    private static final Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

    public IndexHandler() {
        super(String.class);
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExceptionAsFlowControl"})
    protected ProxyResponse<String> processProxyInput(
            String body,
            RequestInfo request,
            Context context
    ) throws ApiGatewayException {

        var resourceIdentifier =  request.getPathParameter(RESOURCE_IDENTIFIER);
        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);
        var commandKind = OpenSearchCommandKind.fromString(resourceIdentifier);
        var query = getQueryString(request);
        query = query.isEmpty() ? EMPTY_STRING : "?" + query;

        try {
            validateResourceIdentifier(resourceIdentifier, commandKind);

            if (body == null && (PUT ==  httpMethod || POST == httpMethod)) {
                throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + "[requestBody]");
            }

            var url = Prefixer.url(workspace,resourceIdentifier) + query;
            var requestBody = Prefixer.body(workspace, resourceIdentifier, body);
            var response = openSearchClient.sendRequest(httpMethod, url, requestBody);

            var responseKind = OpenSearchResponseKind
                .fromString(httpMethod,commandKind,response.getBody());

            var responseBody = PrefixStripper.body(commandKind, responseKind,workspace, response.getBody());
            assertThatLambdaCanHandleResponseSize(responseBody);

            return new ProxyResponse<>(response.getStatus(), responseBody);
        } catch (BadRequestException | RequestTooLargeException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            throw new SearchException(INTERNAL_ERROR, e);
        }
    }

    @Override
    protected void validateRequest(String s, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        // no-op
    }

    private static void assertThatLambdaCanHandleResponseSize(String responseBody) throws RequestTooLargeException {
        int sizeInBytes = responseBody.getBytes(StandardCharsets.UTF_8).length;
        if (sizeInBytes > AWS_LAMBDA_RESPONSE_LIMIT_BYTES) {
            throw new RequestTooLargeException(TOO_LARGE_RESULTS);
        }
    }

    private static String getQueryString(RequestInfo request) {
        return request.getQueryParameters().entrySet().stream()
                .map(entry ->
                        entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
    }

    private static void validateResourceIdentifier(String resourceIdentifier, OpenSearchCommandKind commandKind)
            throws BadRequestException {

        if (commandKind.isNotValid()) {
            throw new BadRequestException(
                    "Root operations and indices starting with '_' or containing anything but letters, digits, '/',"
                    + " '-' or '_'are not allowed. Got: " + resourceIdentifier);
        }
    }

}
