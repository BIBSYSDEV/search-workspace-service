package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.testutils.TestCaseLoader;
import no.unit.nva.stubs.FakeContext;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.GET;
import static no.sikt.sws.testutils.TestConstants.TEST_INDEX_1;
import static no.sikt.sws.testutils.TestUtils.buildPathParamsForIndex;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestUtils.buildRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

class SnapshotRoutineDeletionHandlerTest {


    public static final String SNAPSHOT_INITIALSNAPSHOT_ALL = "_snapshot/"
        +"initialsnapshot/_all";

    public static final String TEST_CASE_FILE = "snapshot/request-snapshot.json";
    private static final Context CONTEXT = new FakeContext();

    private ByteArrayOutputStream output;
    @InjectMocks
    SnapshotRoutineDeletionHandler handler = new SnapshotRoutineDeletionHandler();
    @Mock
    OpenSearchClient openSearchClient;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }


    @Test
    void processInput() throws IOException {

        var responseBody = new TestCaseLoader(TEST_CASE_FILE)
                .getTestCase("GET (all) snapshots")
                .getResponse();
        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, responseBody);

        var pathParams = buildPathParamsForIndex(SNAPSHOT_INITIALSNAPSHOT_ALL);
        var request = buildRequest(HttpMethod.GET, pathParams);
        when(openSearchClient.sendRequest(GET, SNAPSHOT_INITIALSNAPSHOT_ALL, null))
                .thenReturn(mockResponse);


        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

}