package no.sikt.sws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.http.HttpMethodName;
import no.sikt.sws.exception.OpenSearchException;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_PROTOCOL;

public class OpenSearchClient {
    private static final String NULL_STRING = "null";
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClient.class);
    private final AwsClientWrapper awsClientWrapper;
    private final AwsSignerWrapper awsSignerWrapper;

    protected OpenSearchClient(AwsClientWrapper awsClientWrapper, AwsSignerWrapper awsSignerWrapper) {
        this.awsClientWrapper = awsClientWrapper;
        this.awsSignerWrapper = awsSignerWrapper;
    }

    public OpenSearchResponse sendRequest(HttpMethodName httpMethod, String path, String data) {

        var request = buildRequest(httpMethod, path, data);
        signAwsRequest(request);

        try {
            var response = awsClientWrapper.execute(request);
            return new OpenSearchResponse(response.getHttpResponse().getStatusCode(), response.getAwsResponse());

        } catch (OpenSearchException e) {
            logger.info(e.getMessage());
            logger.info("Creating OpenSearchResponse " + e.getStatus() + " " + e.getBody());
            return new OpenSearchResponse(e.getStatus(), e.getBody());
        }


    }

    private Request<Void> buildRequest(HttpMethodName httpMethod, String path, String data) {
        var splitPath = path.split("\\?");
        var endpoint = buildUrl(splitPath[0]);
        var query = splitPath.length > 1 ? splitPath[1] : null;

        logger.info(endpoint + " - " + query);

        Request<Void> request = new DefaultRequest<>("es");
        request.setHttpMethod(httpMethod);
        request.setEndpoint(URI.create(endpoint));

        if (query != null) {
            request.setParameters(getStringListMap(query));
        }

        if (data != null && !NULL_STRING.equals(data)) {
            InputStream inputStream = new ByteArrayInputStream(data.getBytes());
            request.setContent(inputStream);

            var headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            request.setHeaders(headers);
        }

        return request;
    }

    @NotNull
    private Map<String, List<String>> getStringListMap(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> Map.entry(param.split("=")[0], param.split("=")[1]))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())));
    }

    private void signAwsRequest(Request<Void> request) {
        awsSignerWrapper.signRequest(request);
    }

    private String buildUrl(String path) {
        return OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/" + path;
    }

    public static OpenSearchClient passthroughClient() {
        var awsClient = new AwsClientWrapper(true);
        var signer = new AwsSignerWrapper();

        var client = new OpenSearchClient(awsClient, signer);
        return client;
    }

    public static OpenSearchClient defaultClient() {
        var awsClient = new AwsClientWrapper(false);
        var signer = new AwsSignerWrapper();

        var client = new OpenSearchClient(awsClient, signer);
        return client;
    }
}
