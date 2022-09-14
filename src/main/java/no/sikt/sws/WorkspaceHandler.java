package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.http.HttpMethodName.GET;

public class WorkspaceHandler extends ApiGatewayHandler<String, String> {

    public OpenSearchClient openSearchClient = new OpenSearchClient();


    private static final Logger logger = LoggerFactory.getLogger(WorkspaceHandler.class);

    public WorkspaceHandler() {
        super(String.class);
    }

    @Override
    protected String processInput(
            String body,
            RequestInfo request,
            Context context
    ) throws ApiGatewayException {
        ObjectMapper objectMapper = new ObjectMapper();

        String workspace = RequestUtil.getWorkspace(request);

        try {
            JsonNode indexList = objectMapper.readTree(getIndexList(workspace));

            WorkspaceResponse response = new WorkspaceResponse(workspace, indexList);
            return objectMapper.valueToTree(response).toString();
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
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when communicating with opensearch:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(String input, String output) {
        return 200;
    }
}

