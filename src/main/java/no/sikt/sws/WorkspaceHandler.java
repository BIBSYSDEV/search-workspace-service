package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.WorkspaceResponse;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.http.HttpMethodName.GET;

public class WorkspaceHandler extends ApiGatewayHandler<Void, WorkspaceResponse> {

    public OpenSearchClient openSearchClient = OpenSearchClient.defaultClient();


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceHandler.class);
    public static final String INTERNAL_ERROR = "Internal error";

    public WorkspaceHandler() {
        super(Void.class, new Environment());
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        // no op
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
            LOGGER.error("Error when doing WorkspaceResponse.fromValues:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


    private String getIndexList(String workspace) throws SearchException {
        try {
            var url = workspace + "-*";
            LOGGER.info("URL: " + url);
            var response = openSearchClient.sendRequest(GET, url, null);
            LOGGER.info("response-code:" + response.getStatus());
            LOGGER.info("response-body:" + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            throw new SearchException(INTERNAL_ERROR, e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, WorkspaceResponse output) {
        return 200;
    }
}

