package no.sikt.sws;


import com.amazonaws.services.lambda.runtime.Context;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class WorkspaceIndexHandler extends ApiGatewayHandler<Void, IndexResponse> {

    public static final String WORKSPACE_IDENTIFIER = "workspace";
    public static final String RESOURCE_IDENTIFIER = "resource";

    public WorkspaceIndexHandler() {
        super(Void.class);
    }

    @Override
    protected IndexResponse processInput(Void input, RequestInfo request, Context context) throws ApiGatewayException {
        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = request.getPathParameter(WORKSPACE_IDENTIFIER);
        var action = request.getPathParameter(RESOURCE_IDENTIFIER);

        var response = httpMethod + " opensearch/" +workspace + "/" + action;
        System.out.println(response);
        return new IndexResponse(response);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, IndexResponse output) {
        return 200;
    }
}
