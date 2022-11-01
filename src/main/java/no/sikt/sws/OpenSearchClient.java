package no.sikt.sws;

import com.amazonaws.*;
import com.amazonaws.http.*;
import no.sikt.sws.exception.OpenSearchException;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import nva.commons.core.JacocoGenerated;
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

import static no.sikt.sws.constants.ApplicationConstants.*;
import static software.amazon.awssdk.http.HttpStatusCode.*;

public class OpenSearchClient {


    private static final String NULL_STRING = "null";
    private static final List<Integer> FORWARDED_ES_ERROR_CODES
            = Arrays.asList(BAD_REQUEST, NOT_FOUND, METHOD_NOT_ALLOWED, NOT_ACCEPTABLE);

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClient.class);
    private final AmazonHttpClient httpClient;
    private final AwsSignerWrapper awsSignerWrapper;

    private boolean passError;

    protected OpenSearchClient(AmazonHttpClient httpClient, AwsSignerWrapper awsSignerWrapper) {
        this.httpClient = httpClient;
        this.awsSignerWrapper = awsSignerWrapper;
    }

    HttpResponseHandler<String> httpResponseHandler = new HttpResponseHandler<>() {
        @Override
        public String handle(HttpResponse response) throws Exception {
            var bytes = response.getContent().readAllBytes();
            var responseCode = response.getStatusCode();
            var bodyString = new String(bytes);
            logger.info("Handling response: " + responseCode + " " + bodyString);
            return bodyString;
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    };

    HttpResponseHandler<SdkBaseException> errorResponseHandler = new HttpResponseHandler<>() {

        @Override
        public AmazonClientException handle(HttpResponse response) throws Exception {

            var responseCode = response.getStatusCode();
            var bytes = response.getContent().readAllBytes();
            var bodyString = new String(bytes);

            if (passError && FORWARDED_ES_ERROR_CODES.contains(responseCode)) {
                return new OpenSearchException(responseCode, bodyString);
            }


            logger.error("Handling error: " + responseCode + " " + bodyString);

            return new AmazonClientException("OpenSearchError: " + " " + responseCode + " " + bodyString);
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    };



    public OpenSearchResponse sendRequest(HttpMethodName httpMethod, String path, String data) {

        var pathWithoutParams = stripParametersFromPath(path).toString();
        var endpoint = buildUri(pathWithoutParams);
        var query = buildUri(path).getQuery();

        logger.info(endpoint + " - " + query);

        Request<Void> request = new DefaultRequest<>("es");
        request.setHttpMethod(httpMethod);
        request.setEndpoint(endpoint);

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

        signAwsRequest(request);

        try {
            var response = httpClient
                    .requestExecutionBuilder()
                    .executionContext(new ExecutionContext(true))
                    .errorResponseHandler(errorResponseHandler)
                    .request(request)
                    .execute(httpResponseHandler);
            return new OpenSearchResponse(response.getHttpResponse().getStatusCode(), response.getAwsResponse());

        } catch (OpenSearchException e) {
            logger.info(e.getMessage());
            logger.info("Creating OpenSearchResponse " + e.getStatus() + " " + e.getBody());
            return new OpenSearchResponse(e.getStatus(), e.getBody());
        }


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

    private URI buildUri(String path) {
        return URI.create(OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/" + path);
    }

    @JacocoGenerated
    public void setPassError(boolean passError) {
        this.passError = passError;
    }

    public static OpenSearchClient passthroughClient() {
        var httpClient
                = AmazonHttpClient.builder().clientConfiguration(new ClientConfiguration()).build();
        var signer = new AwsSignerWrapper();

        var client = new OpenSearchClient(httpClient, signer);
        client.setPassError(true);
        return client;
    }

    public static OpenSearchClient defaultClient() {
        var httpClient
                = AmazonHttpClient.builder().clientConfiguration(new ClientConfiguration()).build();
        var signer = new AwsSignerWrapper();

        var client = new OpenSearchClient(httpClient, signer);
        return client;
    }

    private URI stripParametersFromPath(String path) {
        try {
            URI uri = new URI(path);
            return new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null, // Ignore the query part of the input url
                    uri.getFragment());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
