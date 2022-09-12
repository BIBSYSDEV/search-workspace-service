package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class WorkspaceIndexHandler extends ApiGatewayHandler<String, IndexResponse> {

    public static final String RESOURCE_IDENTIFIER = "resource";
    public OpenSearchClient openSearchClient = new OpenSearchClient();


    private static final Logger logger = LoggerFactory.getLogger(WorkspaceIndexHandler.class);

    public WorkspaceIndexHandler() {
        super(String.class);
    }

    @Override
    protected IndexResponse processInput(String body, RequestInfo request, Context context) throws ApiGatewayException {

        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);
        var index = request.getPathParameter(RESOURCE_IDENTIFIER);

        try {
            var url = workspace + "-" + index;
            logger.info("URL: " + url);

            var response = openSearchClient.sendRequest(
                    httpMethod,
                    url,
                    RequestUtil.addWorkspace(body, workspace, index));
            logger.info("response-code:" + response.getStatus());

            var responseBody = ResponseUtil.stripWorkspace(response.getBody(), workspace);
            logger.info("response-body:" + responseBody);

            var jsonResult = new JSONObject();
            jsonResult.put("message", responseBody);

            return new IndexResponse(jsonResult);
        } catch (Exception e) {
            logger.error("Error when communicating with opensearch:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }



    @Override
    protected Integer getSuccessStatusCode(String input, IndexResponse output) {
        return 200;
    }
}
