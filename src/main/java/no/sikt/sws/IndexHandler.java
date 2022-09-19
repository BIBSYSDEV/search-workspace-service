package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.utils.Pair;


/**
 * Created for checking if external libraries have been imported properly.
 */
public class IndexHandler extends OpenSearchProxyHandler<String, String> {

    public static final String RESOURCE_IDENTIFIER = "resource";
    public OpenSearchClient openSearchClient = new OpenSearchClient();


    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

    public IndexHandler() {
        super(String.class);
    }

    @Override
    Pair<String, Integer> inputToOutputWithStatus(String body, RequestInfo request, Context context)
            throws ApiGatewayException {
        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);
        var resourceIdentifier = request.getPathParameter(RESOURCE_IDENTIFIER);

        try {
            var url = workspace + "-" + resourceIdentifier;
            logger.info("URL: " + url);

            var response = openSearchClient.sendRequest(
                    httpMethod,
                    url,
                    body);
            //out of scope
            //WorkspaceStripper.add(body, workspace, resourceIdentifier));
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());


            var responseBody = WorkspaceStripper.remove(response.getBody(), workspace);
            logger.info("response-body:" + responseBody);

            return Pair.of(responseBody, response.getStatus());
        } catch (Exception e) {
            logger.error("Error when communicating with opensearch:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


}
