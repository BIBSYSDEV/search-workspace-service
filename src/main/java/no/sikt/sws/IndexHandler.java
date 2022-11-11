package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.opensearch.OpenSearchCommandKind;
import no.sikt.sws.models.opensearch.OpenSearchResponseKind;
import nva.commons.apigateway.ApiGatewayProxyHandler;
import nva.commons.apigateway.ProxyResponse;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static com.amazonaws.http.HttpMethodName.POST;
import static com.amazonaws.http.HttpMethodName.PUT;
import static no.sikt.sws.constants.ApplicationConstants.REQUIRED_PARAMETER_IS_NULL;

/**
 * Created for checking if external libraries have been imported properly.
 */
public class IndexHandler extends ApiGatewayProxyHandler<String, String> {

    public static final String RESOURCE_IDENTIFIER = "resource";
    // Regex: may only contain big and small latin letters, norwegian letters, digits, '/', '-' and '_'
    private static final String ALLOWED_INPUT = "[A-Za-zÆØÅæøå\\d/&?=_-]*";
    public static final String INTERNAL_ERROR = "Internal error";
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

        var resourceIdentifier =  request.getPathParameter(RESOURCE_IDENTIFIER);
        var httpMethod = RequestUtil.getRequestHttpMethod(request);
        var workspace = RequestUtil.getWorkspace(request);
        var commandKind = OpenSearchCommandKind.fromString(resourceIdentifier);
        var query = getQueryString(request);
        query = query.isEmpty() ? EMPTY_STRING : "?" + query;

        try {
            validateResourceIdentifier(resourceIdentifier);

            var url = Prefixer.url(workspace,resourceIdentifier) + query;
            logger.info("URL: " + url);

            if (body == null && (PUT ==  httpMethod || POST == httpMethod)) {
                throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + "[requestBody]");
            }

            var requestBody = Prefixer.body(workspace, resourceIdentifier, body);
            var response = openSearchClient.sendRequest(httpMethod, url, requestBody);

            logger.info("response-code:" + response.getStatus());
            logger.info("raw response-body:" + response.getBody());

            var responseKind = OpenSearchResponseKind
                .fromString(httpMethod,commandKind,response.getBody());

            var responseBody = PrefixStripper.body(commandKind, responseKind,workspace, response.getBody());

            logger.info("stripped response-body:" + responseBody);

            return new ProxyResponse<>(response.getStatus(), responseBody);
        } catch (BadRequestException be) {
            logger.error(be.getLocalizedMessage());
            throw be;
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            throw new SearchException(INTERNAL_ERROR, e);
        }
    }

    @NotNull
    private static String getQueryString(RequestInfo request) {
        return request.getQueryParameters().entrySet().stream()
                .map(entry ->
                        entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
    }

    private static void validateResourceIdentifier(String resourceIdentifier) throws BadRequestException {
        if (resourceIdentifier.startsWith("_") || !resourceIdentifier.matches(ALLOWED_INPUT)) {
            throw new BadRequestException(
                    "Root operations and indeces starting with '_' or containing anything but letters, digits, '/',"
                            + " '-' or '_'are not allowed. Got: " + resourceIdentifier);
        }
    }

}
