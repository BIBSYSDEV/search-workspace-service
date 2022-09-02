package no.sikt.sws;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.*;

import java.io.IOException;
import java.net.URI;

public class RestClientOpenSearch {


    public Response sendRequest(HttpMethod httpMethod, String url) throws IOException {

        Request<Void> request = new DefaultRequest<>("es"); //Request to ElasticSearch
        request.setHttpMethod(HttpMethodName.GET);
        request.setEndpoint(URI.create("https://" + url));

        var awsSigner = new AWS4Signer();
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
            public SdkBaseException handle(HttpResponse response) throws Exception {
                System.out.println("Handling error: " + response.toString());
                return null;
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
}
