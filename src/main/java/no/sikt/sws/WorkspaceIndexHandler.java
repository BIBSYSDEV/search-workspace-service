package no.sikt.sws;


import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;

import java.io.IOException;

import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;

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
        var index = request.getPathParameter(RESOURCE_IDENTIFIER);
        var url = OPENSEARCH_ENDPOINT_ADDRESS + "/" + workspace + "-" + index;


        var restClientOpenSearch = new RestClientOpenSearch();
        try {
            var response = restClientOpenSearch.sendRequest(httpMethod, url);
            System.out.println("response-object:" + response.toString());
            System.out.println("response value:" + response.getAwsResponse());

            return new IndexResponse(response.getAwsResponse());

        } catch (IOException e) {
            e.printStackTrace();
            throw new SearchException(e.getMessage(), e);
        }
    }


    @Override
    protected Integer getSuccessStatusCode(Void input, IndexResponse output) {
        return 200;
    }
}
