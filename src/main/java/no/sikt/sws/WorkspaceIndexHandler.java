package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class WorkspaceIndexHandler extends ApiGatewayHandler<Void, IndexResponse> {

    public static final String RESOURCE_IDENTIFIER = "resource";

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceIndexHandler.class);

    public WorkspaceIndexHandler() {
        super(Void.class);
    }

    @Override
    protected IndexResponse processInput(Void input, RequestInfo request, Context context) throws ApiGatewayException {

        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);
        var index = request.getPathParameter(RESOURCE_IDENTIFIER);

        var restClientOpenSearch = new OpenSearchClient();
        try {
            var url = OPENSEARCH_ENDPOINT_ADDRESS + "/" + workspace + "-" + index;
            logger.info("URL: "+url);
            var response = restClientOpenSearch.sendRequest(httpMethod, url);
            logger.info("response-object:" + response.toString());
            logger.info("response value:" + response.getAwsResponse());

            return new IndexResponse(response.getAwsResponse().toString());

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
