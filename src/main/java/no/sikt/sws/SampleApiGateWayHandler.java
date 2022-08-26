package no.sikt.sws;


import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;

/**
 * Created for checking if external libraries have been imported properly
 */
public class SampleApiGateWayHandler extends ApiGatewayHandler<Void, Void> {

    public SampleApiGateWayHandler() {
        super(Void.class);
    }

    @Override
    protected Void processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        System.out.println("Inner function processInput called from lambda");
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Void output) {
        return 200;
    }
}
