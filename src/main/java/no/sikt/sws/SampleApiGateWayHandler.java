package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;

/**
 * Created for checking if external libraries have been imported properly
 */
public class SampleApiGateWayHandler extends ApiGatewayHandler<Void, Void> {

    public SampleApiGateWayHandler(Class<Void> iclass) {
        super(iclass);
    }

    @Override
    protected Void processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Void output) {
        return null;
    }
}
