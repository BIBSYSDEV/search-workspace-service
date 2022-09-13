package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import software.amazon.awssdk.utils.Pair;


public abstract class OpenSearchProxyHandler<I, O> extends ApiGatewayHandler<I, O>  {

    private Integer statusCode;

    public OpenSearchProxyHandler(Class<I> iclass) {
        this(iclass, new Environment());
    }

    public OpenSearchProxyHandler(Class<I> iclass, Environment environment) {
        super(iclass, environment);
    }

    @Override
    protected O processInput(I input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        Pair<O, Integer> result = inputToOutputWithStatus(input, requestInfo, context);
        statusCode = result.right();
        return result.left();
    }

    @Override
    protected Integer getSuccessStatusCode(I input, O output) {
        if (statusCode == null) {
            throw new IllegalStateException("getSuccessStatusCode was called before processInput");
        }
        return statusCode;
    }

    abstract Pair<O, Integer> inputToOutputWithStatus(I input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException;
}
