package no.sikt.sws;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.*;
import no.sikt.sws.exception.OpenSearchException;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static no.sikt.sws.constants.ApplicationConstants.ELASTICSEARCH_REGION;
import static software.amazon.awssdk.http.HttpStatusCode.BAD_REQUEST;
import static software.amazon.awssdk.http.HttpStatusCode.NOT_FOUND;

public class OpenSearchClient {


    private static final String ELASTIC_SEARCH_SERVICE_NAME = "es";
    private static final List<Integer> FORWARDED_ES_ERROR_CODES = Arrays.asList(BAD_REQUEST, NOT_FOUND);

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClient.class);

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

            if (FORWARDED_ES_ERROR_CODES.contains(responseCode)) {
                return new OpenSearchException(response);
            }

            var bytes = response.getContent().readAllBytes();
            var bodyString = new String(bytes);
            logger.error("Handling error: " + responseCode + " " + bodyString);

            return new AmazonClientException("OpenSearchError: "+ " " + responseCode +" " +bodyString);
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    };

    public OpenSearchResponse sendRequest(HttpMethodName httpMethod, String url) throws IOException {

        Request<Void> request = new DefaultRequest<>("es"); //Request to ElasticSearch
        request.setHttpMethod(httpMethod);
        request.setEndpoint(URI.create("https://" + url));

        var awsSigner = getAws4Signer();
        new DefaultAWSCredentialsProviderChain().getCredentials();

        var credentials = new DefaultAWSCredentialsProviderChain().getCredentials();

        awsSigner.sign(request, credentials);

        try {
            var response = new AmazonHttpClient(new ClientConfiguration())
                    .requestExecutionBuilder()
                    .executionContext(new ExecutionContext(true))
                    .errorResponseHandler(errorResponseHandler)
                    .request(request)
                    .execute(httpResponseHandler);
            return new OpenSearchResponse(response.getHttpResponse().getStatusCode(), response.getAwsResponse());

        } catch (OpenSearchException e) {
            return new OpenSearchResponse(e.getResponse());
        }


    }



    @JacocoGenerated
    private AWS4Signer getAws4Signer() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(ELASTIC_SEARCH_SERVICE_NAME);
        signer.setRegionName(ELASTICSEARCH_REGION);
        return signer;
    }
}
