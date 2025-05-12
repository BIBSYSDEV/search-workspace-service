package no.sikt.sws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.http.HttpMethodName;
import no.sikt.sws.exception.OpenSearchException;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
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

@SuppressWarnings({"PMD.OnlyOneReturn"})
public class OpenSearchClient {
    private static final String NULL_STRING = "null";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSearchClient.class);
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
            LOGGER.info(e.getMessage());
            LOGGER.info("Creating OpenSearchResponse " + e.getStatus() + " " + e.getBody());
            return new OpenSearchResponse(e.getStatus(), e.getBody());
        }


    }

    private Request<Void> buildRequest(HttpMethodName httpMethod, String path, String data) {

        var splitPath = path.split("\\?", 2);
        var endpoint = splitPath[0];
        var query = splitPath.length > 1 ? splitPath[1] : null;


        LOGGER.info(endpoint + " - " + query);

        Request<Void> request = new DefaultRequest<>("es");
        request.setHttpMethod(httpMethod);
        request.setEndpoint(buildUri(endpoint));

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

    private Map<String, List<String>> getStringListMap(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> Map.entry(param.split("=")[0], param.split("=")[1]))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())));
    }

    private void signAwsRequest(Request<Void> request) {
        awsSignerWrapper.signRequest(request);
    }

    private URI buildUri(String path) {
        return URI.create(OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/" + path);
    }

    public static OpenSearchClient passthroughClient() {
        var awsClient = new AwsClientWrapper(true);
        var signer = new AwsSignerWrapper();

        return new OpenSearchClient(awsClient, signer);
    }

    public static OpenSearchClient defaultClient() {
        var awsClient = new AwsClientWrapper(false);
        var signer = new AwsSignerWrapper();

        return new OpenSearchClient(awsClient, signer);
    }

}
