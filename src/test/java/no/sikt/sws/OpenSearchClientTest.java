package no.sikt.sws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.http.HttpResponse;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.testutils.FakeAwsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.amazonaws.http.HttpMethodName.*;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;
import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_PROTOCOL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenSearchClientTest {

    final HttpResponse httpResponse = mock(HttpResponse.class);
    final AwsSignerWrapper awsSigner = mock(AwsSignerWrapper.class);

    @BeforeEach
    public void setup() {

        var content = "{}";
        var statusCode = 200;

        when(httpResponse.getStatusCode()).thenReturn(statusCode);
        when(httpResponse.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));

    }

    @Test
    void testPlainGet() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test";

        Request<Void> request = new DefaultRequest<>("es");
        request.setHttpMethod(GET);
        request.setEndpoint(URI.create(expectedUri));

        var openSearchClient = new OpenSearchClient(new FakeAwsClient(request, httpResponse), awsSigner);
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

        var openSearchClient = new OpenSearchClient(new FakeAwsClient(expectedRequest, httpResponse), awsSigner);
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

        var openSearchClient = new OpenSearchClient(new FakeAwsClient(expectedRequest, httpResponse), awsSigner);
        OpenSearchResponse response = openSearchClient.sendRequest(POST, "sondre-test/_search?size=1", null);
        assertEquals(200, response.getStatus());
    }

    @Test
    @Disabled("Marina will fix in upcoming task")
    void testAdvancedQueryParameters() {
        var expectedUri = OPENSEARCH_ENDPOINT_PROTOCOL + "://" + OPENSEARCH_ENDPOINT_ADDRESS + "/sondre-test/_search";

        Request<Void> expectedRequest = new DefaultRequest<>("es");
        expectedRequest.setHttpMethod(POST);
        expectedRequest.setEndpoint(URI.create(expectedUri));
        expectedRequest.setParameters(Map.of("q", List.of("age:>33")));

        var openSearchClient = new OpenSearchClient(new FakeAwsClient(expectedRequest, httpResponse), awsSigner);
        OpenSearchResponse response = openSearchClient.sendRequest(POST, "sondre-test/_search?q=age:>33", null);
        assertEquals(200, response.getStatus());
    }

}