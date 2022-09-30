package no.sikt.sws;

import com.amazonaws.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
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
        when(mockHttpResponse.getContent())
                .thenReturn(new ByteArrayInputStream("".getBytes()));

        final OpenSearchResponse response = new OpenSearchResponse(mockHttpResponse);

        when(mockOpenSearchClient.sendRequest(PUT, "fredrik-test", null))
                .thenReturn(response);
    }

    @Test
    void testMockingOfOpenSearchClient() throws IOException {
        OpenSearchResponse response = mockOpenSearchClient.sendRequest(PUT, "fredrik-test", null);

        assertEquals(200, response.getStatus());
    }

}