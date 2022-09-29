package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayProxyHandler;
import nva.commons.apigateway.ProxyResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class IndexHandler extends ApiGatewayProxyHandler<String, String> {

    public static final String RESOURCE_IDENTIFIER = "resource";
    public OpenSearchClient openSearchClient = OpenSearchClient.passthroughClient();


    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

    public IndexHandler() {
        super(String.class);
    }

    @Override
    protected ProxyResponse<String> processProxyInput(
            String body,
            RequestInfo request,
            Context context
    ) throws ApiGatewayException {

        var resourceIdentifier = request.getPathParameter(RESOURCE_IDENTIFIER);

        // Regex: may only contain big and small letters, digits, '/', '?', '&', '-' and '_'
        var regex = "[A-Za-z\\d/?&_-]*";

        if (resourceIdentifier.startsWith("_") || !resourceIdentifier.matches(regex)) {
            throw new BadRequestException(
                    "Root operations and indeces starting with '_' are not allowed. Got: " + resourceIdentifier);
        }

        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);

        try {
            var url = WorkspaceStripper.prefixUrl(resourceIdentifier, workspace);

            logger.info("URL: " + url);

            var response = openSearchClient.sendRequest(httpMethod, url, body);

            logger.info("response-code:" + response.getStatus());
            logger.info("raw response-body:" + response.getBody());

            var responseBody = WorkspaceStripper.remove(response.getBody(), workspace);
            logger.info("stripped response-body:" + responseBody);

            return new ProxyResponse<>(response.getStatus(), responseBody);
        } catch (Exception e) {
            logger.error("Error when communicating with opensearch:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }
}
