package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
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
import static no.sikt.sws.testutils.TestUtils.buildPathParamsForIndex;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestUtils.buildRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

class SnapshotRoutineDeletionHandlerTest {


    public static final String GET_ALL_URL = "_snapshot/initialsnapshot/_all";

    public static final String TEST_CASE_FILE = "snapshot/request-snapshot.json";
    private static final Context CONTEXT = new FakeContext();
    public static final String DELETE_URL_SNAP = "_snapshot/initialsnapshot/snap1665487673861";

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
        var testCase = new TestCaseLoader(TEST_CASE_FILE)
                .getTestCase("GET (all) snapshots");
        var responseBody = testCase.getResponse();
        var responseStripped = testCase.getResponseStripped();

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, responseBody);
        final OpenSearchResponse mockDeleteResponse = new OpenSearchResponse(200, responseStripped);

        var pathParams = buildPathParamsForIndex(GET_ALL_URL);
        var request = buildRequest(HttpMethod.GET, pathParams);
        when(openSearchClient.sendRequest(GET, GET_ALL_URL, null))
                .thenReturn(mockResponse);
        when(openSearchClient.sendRequest(HttpMethodName.DELETE, DELETE_URL_SNAP, null))
                        .thenReturn(mockDeleteResponse);

        handler.handleRequest(request, output, CONTEXT);



        var response = GatewayResponse.fromOutputStream(output, String.class);


        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

}