package no.sikt.sws;

import com.amazonaws.Response;
import com.amazonaws.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.PUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenSearchClientTest {


    private final OpenSearchClient mockOpenSearchClient = mock(OpenSearchClient.class);


    @BeforeEach
    public void setup() throws IOException {

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.getStatusCode())
                .thenReturn(200);

        final Response<String> mockResponse = new Response("MockResponse", mockHttpResponse);

        when(mockOpenSearchClient.sendRequest(PUT, "fredrik-test"))
                .thenReturn(mockResponse);
    }

    @Test
    void testMockingOfOpenSearchClient() throws IOException {
        Response response = mockOpenSearchClient.sendRequest(PUT, "fredrik-test");

        assertEquals (200, response.getHttpResponse().getStatusCode());
    }

}
