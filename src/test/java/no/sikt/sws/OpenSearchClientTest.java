package no.sikt.sws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.http.HttpResponse;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.amazonaws.http.HttpMethodName.GET;
import static com.amazonaws.http.HttpMethodName.POST;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_PROTOCOL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenSearchClientTest {

    final HttpResponse httpResponse = mock(HttpResponse.class);
    final AwsClientWrapper awsClient = mock(AwsClientWrapper.class);
    final AwsSignerWrapper awsSigner = mock(AwsSignerWrapper.class);

    final Response<String> emptyResponse = new Response<>("", httpResponse);

    private OpenSearchClient openSearchClient;

    @BeforeEach
    public void setup() {

        openSearchClient = new OpenSearchClient(awsClient, awsSigner);


        var content = "{}";
        var statusCode = 200;

        when(httpResponse.getStatusCode()).thenReturn(statusCode);
        when(httpResponse.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
    }

    @Test
    void testPlainGet() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test";

        Request<Void> expectedRequest = new DefaultRequest<>("es");
        expectedRequest.setHttpMethod(GET);
        expectedRequest.setEndpoint(URI.create(expectedUri));

        when(awsClient.execute(argThat(new RequestMatcher(expectedRequest)))).thenReturn(emptyResponse);

        OpenSearchResponse response = openSearchClient.sendRequest(GET, "sondre-test", null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testPostBody() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test/_bulk";
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());

        Request<Void> expectedRequest = new DefaultRequest<>("es");
        expectedRequest.setHttpMethod(POST);
        expectedRequest.setEndpoint(URI.create(expectedUri));
        expectedRequest.setContent(inputStream);

        when(awsClient.execute(argThat(new RequestMatcher(expectedRequest)))).thenReturn(emptyResponse);

        OpenSearchResponse response = openSearchClient.sendRequest(POST, "sondre-test/_bulk", "{}");
        assertEquals(200, response.getStatus());
    }

    @Test
    void testBasicQueryParameters() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test/_search";

        Request<Void> expectedRequest = new DefaultRequest<>("es");
        expectedRequest.setHttpMethod(POST);
        expectedRequest.setEndpoint(URI.create(expectedUri));
        expectedRequest.setParameters(Map.of("size", List.of("1")));

        when(awsClient.execute(argThat(new RequestMatcher(expectedRequest)))).thenReturn(emptyResponse);

        OpenSearchResponse response = openSearchClient.sendRequest(POST, "sondre-test/_search?size=1", null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testAdvancedQueryParameters() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test/_search";

        Request<Void> expectedRequest = new DefaultRequest<>("es");
        expectedRequest.setHttpMethod(POST);
        expectedRequest.setEndpoint(URI.create(expectedUri));
        expectedRequest.setParameters(Map.of("q", List.of("age:>33")));

        when(awsClient.execute(argThat(new RequestMatcher(expectedRequest)))).thenReturn(emptyResponse);

        OpenSearchResponse response = openSearchClient.sendRequest(POST, "sondre-test/_search?q=age:>33", null);
        assertEquals(200, response.getStatus());
    }
}