package no.sikt.sws;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.*;
import nva.commons.core.JacocoGenerated;

import java.io.IOException;
import java.net.URI;

import static no.sikt.sws.constants.ApplicationConstants.ELASTICSEARCH_REGION;

public class RestClientOpenSearch {


    private static final String ELASTIC_SEARCH_SERVICE_NAME = "es";

    public Response sendRequest(HttpMethod httpMethod, String url) throws IOException {

        Request<Void> request = new DefaultRequest<>("es"); //Request to ElasticSearch
        request.setHttpMethod(HttpMethodName.GET);
        request.setEndpoint(URI.create("https://" + url));

        var awsSigner = getAws4Signer();
        new DefaultAWSCredentialsProviderChain().getCredentials();

        var credentials = new DefaultAWSCredentialsProviderChain().getCredentials();

        awsSigner.sign(request, credentials);

        var httpResponseHandler = new HttpResponseHandler<String>() {
            @Override
            public String handle(HttpResponse response) throws Exception {
                System.out.println("Handling response: " + response.toString());
                return response.toString();
            }

            @Override
            public boolean needsConnectionLeftOpen() {
                return false;
            }
        };

        var errorResponseHandler = new HttpResponseHandler<SdkBaseException>() {

            @Override
            public AmazonClientException handle(HttpResponse response) throws Exception {
                var bytes = response.getContent().readAllBytes();
                var responseCode = response.getStatusCode();
                var bodyString = new String(bytes);
                System.out.println("Handling error: " + responseCode + " " + bodyString);
                return new AmazonClientException("OpenSearchError: "+ " " + responseCode +" " +bodyString);
            }

            @Override
            public boolean needsConnectionLeftOpen() {
                return false;
            }
        };

        Response<String> rsp = new AmazonHttpClient(new ClientConfiguration())
                .requestExecutionBuilder()
                .executionContext(new ExecutionContext(true))
                .request(request)
                .errorResponseHandler(errorResponseHandler)
                .execute(httpResponseHandler);

        return rsp;
    }

    @JacocoGenerated
    private AWS4Signer getAws4Signer() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(ELASTIC_SEARCH_SERVICE_NAME);
        signer.setRegionName(ELASTICSEARCH_REGION);
        return signer;
    }
}
