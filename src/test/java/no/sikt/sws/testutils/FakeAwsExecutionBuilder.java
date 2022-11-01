package no.sikt.sws.testutils;

import com.amazonaws.*;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FakeAwsExecutionBuilder<T> implements AmazonHttpClient.RequestExecutionBuilder {


    private Request<String> expectedRequest;
    private HttpResponse response;
    private boolean requestCalled;

    public FakeAwsExecutionBuilder(Request<String> expectedRequest, HttpResponse response) {
        this.expectedRequest = expectedRequest;
        this.response = response;
    }

    @Override
    public AmazonHttpClient.RequestExecutionBuilder request(Request<?> request) {
        assertEquals(this.expectedRequest.getEndpoint(), request.getEndpoint());
        assertEquals(this.expectedRequest.getParameters(), request.getParameters());

        if (expectedRequest.getContent() != null) {
            String excepctedContent = new BufferedReader(new InputStreamReader(expectedRequest.getContent()))
                    .lines().collect(Collectors.joining("\n"));

            String actualContent = new BufferedReader(new InputStreamReader(request.getContent()))
                    .lines().collect(Collectors.joining("\n"));

            assertEquals(excepctedContent, actualContent);
        }

        this.requestCalled = true;
        return this;
    }

    @Override
    public AmazonHttpClient.RequestExecutionBuilder
            errorResponseHandler(HttpResponseHandler<? extends SdkBaseException> errorResponseHandler) {
        return this;
    }

    @Override
    public AmazonHttpClient.RequestExecutionBuilder executionContext(ExecutionContext executionContext) {
        return this;
    }

    @Override
    public AmazonHttpClient.RequestExecutionBuilder requestConfig(RequestConfig requestConfig) {
        return this;
    }

    @Override
    public <ReturnT> Response<ReturnT> execute(HttpResponseHandler<ReturnT> responseHandler) {
        assertTrue(requestCalled, "request() should have been called");
        return new Response<>(null, this.response);
    }

    @Override
    public Response<Void> execute() {
        assertTrue(requestCalled, "request() should have been called");
        return new Response<>(null, this.response);
    }
}
