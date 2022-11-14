package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.models.opensearch.SearchDto;
import no.sikt.sws.testutils.TestCaseLoader;
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
import java.net.URI;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.PrefixStripperTest.WORKSPACEPREFIX;
import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.sikt.sws.testutils.TestUtils.buildPathParamsForIndex;
import static no.sikt.sws.testutils.TestUtils.buildRequestWithBody;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class AggregationTest extends TestCase {

    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;


    @InjectMocks
    IndexHandler handler = new IndexHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }

    @Test
    void shouldHandleSearchRequestWithQueryParameters() throws IOException {

        var testcase =
                new TestCaseLoader("proxy/requests-aggregation.json")
                        .getTestCase("GET search with aggregation");
        var requestOpensearch = testcase.getRequestOpensearch();
        var requestGateway = testcase.getRequestGateway();

        when(openSearchClient.sendRequest(
                requestOpensearch.getMethod(),
                requestOpensearch.getUrl(),
                requestOpensearch.getBody())
        ).thenReturn(new OpenSearchResponse(200, testcase.getResponse()));

        var gatewayUrl = URI.create(requestGateway.getUrl());
        var pathParams = buildPathParamsForIndex(gatewayUrl.getPath());
        var request = buildRequestWithBody(HttpMethod.GET, pathParams, requestGateway.getBody());

        handler.handleRequest(request, output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, String.class);
        var resultBody =  SearchDto
                .fromResponse(response.getBody())
                .stripper(WORKSPACEPREFIX)
                .toJsonCompact();

        Assertions.assertEquals(compact(testcase.getResponseStripped()), compact(resultBody));

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    private String compact(String body) {
        return body.replaceAll("[\n\r ]", EMPTY_STRING);
    }
}
