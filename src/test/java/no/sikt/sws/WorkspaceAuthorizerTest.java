package no.sikt.sws;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.unit.nva.stubs.FakeContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.PUT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkspaceAuthorizerTest  extends TestCase {

    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;

    @InjectMocks
    WorkspaceIndexHandler handler = new WorkspaceIndexHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @BeforeEach
    public void beforeEach() throws IOException {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }

    private void setup() throws IOException {
        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.getStatusCode())
                .thenReturn(200);
        when(mockHttpResponse.getContent())
                .thenReturn(new ByteArrayInputStream("".getBytes()));

        final OpenSearchResponse mockResponse = new OpenSearchResponse(mockHttpResponse);

        when(openSearchClient.sendRequest(PUT, "workspace-sondre-index1/_mapping"))
                .thenReturn(mockResponse);
    }


//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void authorized() {
    }

    @Test
    void addUserWorkspace() {
    }
}