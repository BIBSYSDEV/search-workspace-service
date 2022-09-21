package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.http.HttpMethodName.GET;

public class WorkspaceHandler extends ApiGatewayHandler<Void, WorkspaceResponse> {

    public final OpenSearchClient openSearchClient = new OpenSearchClient();


    private static final Logger logger = LoggerFactory.getLogger(WorkspaceHandler.class);

    public WorkspaceHandler() {
        super(Void.class);
    }

    @Override
    protected WorkspaceResponse processInput(
            Void input,
            RequestInfo request,
            Context context
    ) throws ApiGatewayException {

        String workspace = RequestUtil.getWorkspace(request);
        String indexList = getIndexList(workspace);

        try {
            return WorkspaceResponse.fromValues(workspace, indexList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SearchException(e.getMessage(), e);
        }
    }


    private String getIndexList(String workspace) throws SearchException {
        try {
            var url = workspace + "-*";
            logger.info("URL: " + url);
            var response = openSearchClient.sendRequest(GET, url, null);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());

            var responseBody = WorkspaceStripper.remove(response.getBody(), workspace);
            logger.info("body-stripped:" + responseBody);
            return responseBody;
        } catch (Exception e) {
            logger.error("Error when communicating with opensearch:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, WorkspaceResponse output) {
        return 200;
    }
}

