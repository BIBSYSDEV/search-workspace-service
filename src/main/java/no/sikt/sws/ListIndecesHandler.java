package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListIndecesHandler extends ApiGatewayHandler<String, String> {

    public static final String RESOURCE_IDENTIFIER = "resource";
    public OpenSearchClient openSearchClient = new OpenSearchClient();


    private static final Logger logger = LoggerFactory.getLogger(ListIndecesHandler.class);

    public ListIndecesHandler() {
        super(String.class);
    }

    @Override
    protected String processInput(
            String body,
            RequestInfo request,
            Context context
    ) throws ApiGatewayException {

        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);


        try {
            var url = workspace + "-*";
            logger.info("URL: " + url);
            var response = openSearchClient.sendRequest(httpMethod, url, body);
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

