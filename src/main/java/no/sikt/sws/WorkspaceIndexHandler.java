package no.sikt.sws;


import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class WorkspaceIndexHandler extends ApiGatewayHandler<Void, Void> {

    public WorkspaceIndexHandler() {
        super(Void.class);
    }

    @Override
    protected Void processInput(Void input, RequestInfo request, Context context) throws ApiGatewayException {
        System.out.println("Lambda called");
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Void output) {
        return 200;
    }
}
