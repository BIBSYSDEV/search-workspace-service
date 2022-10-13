package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.models.opensearch.WorkspaceResponse;
import no.sikt.sws.testutils.TestUtils;
import no.unit.nva.stubs.FakeContext;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.GET;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestConstants.OPEN_SEARCH_INDEX_LIST;
import static no.sikt.sws.testutils.TestConstants.TEST_WORKSPACE_PREFIX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class WorkspaceHandlerTest extends TestCase {


    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;

    @InjectMocks
    WorkspaceHandler handler = new WorkspaceHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }

    @Test
    void callToOpenSearchMapsToCorrectResponse() throws IOException {
        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, OPEN_SEARCH_INDEX_LIST);

        when(openSearchClient.sendRequest(GET, TEST_WORKSPACE_PREFIX + "*", null))
                .thenReturn(mockResponse);

        var request = TestUtils.buildRequest(HttpMethod.GET, null);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, WorkspaceResponse.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));

        WorkspaceResponse workspaceResponse = response.getBodyObject(WorkspaceResponse.class);

        Assertions.assertNotNull(workspaceResponse.indexList);
        Assertions.assertNotNull(workspaceResponse.accountIdentifier);

    }


}
