package no.sikt.sws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.SdkBaseException;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import no.sikt.sws.exception.OpenSearchException;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static no.sikt.sws.constants.ApplicationConstants.ELASTICSEARCH_REGION;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_PROTOCOL;
import static software.amazon.awssdk.http.HttpStatusCode.BAD_REQUEST;
import static software.amazon.awssdk.http.HttpStatusCode.NOT_ACCEPTABLE;
import static software.amazon.awssdk.http.HttpStatusCode.NOT_FOUND;

public class OpenSearchClient {


    private static final String ELASTIC_SEARCH_SERVICE_NAME = "es";
    private static final String NULL_STRING = "null";
    private static final List<Integer> FORWARDED_ES_ERROR_CODES
            = Arrays.asList(BAD_REQUEST, NOT_FOUND, NOT_ACCEPTABLE);

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClient.class);

    private boolean passError;

    HttpResponseHandler<String> httpResponseHandler = new HttpResponseHandler() {
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

    HttpResponseHandler<SdkBaseException> errorResponseHandler = new HttpResponseHandler() {

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

    public OpenSearchResponse sendRequest(HttpMethodName httpMethod, String path, String data) throws IOException {

        Request<Void> request = new DefaultRequest<>("es");
        request.setHttpMethod(httpMethod);
        request.setEndpoint(buildUri(path));

        if (data != null && !NULL_STRING.equals(data)) {
            InputStream inputStream = new ByteArrayInputStream(data.getBytes());
            request.setContent(inputStream);

            var headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            request.setHeaders(headers);
        }

        signAwsRequest(request);

        try {
            var response = new AmazonHttpClient(new ClientConfiguration())
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

    private void signAwsRequest(Request<Void> request) {
        var awsSigner = getAws4Signer();
        var credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        awsSigner.sign(request, credentials);
    }

    private URI buildUri(String path) {
        return URI.create(OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + path);
    }


    @JacocoGenerated
    private AWS4Signer getAws4Signer() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(ELASTIC_SEARCH_SERVICE_NAME);
        signer.setRegionName(ELASTICSEARCH_REGION);
        return signer;
    }

    @JacocoGenerated
    public void setPassError(boolean passError) {
        this.passError = passError;
    }

    public static OpenSearchClient passthroughClient() {
        var client = new OpenSearchClient();
        client.setPassError(true);
        return client;
    }
}
