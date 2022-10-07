package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.sikt.sws.testutils.TestUtils;
import no.unit.nva.commons.json.JsonUtils;
import no.sikt.sws.testutils.JsonStringMatcher;
import no.unit.nva.stubs.FakeContext;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestConstants.TEST_INDEX_1;
import static no.sikt.sws.testutils.TestConstants.TEST_WORKSPACE_PREFIX;
import static no.sikt.sws.testutils.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class IndexHandlerTest extends TestCase {

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
    void shouldGiveResponse() throws IOException {

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, "{}");

        when(openSearchClient.sendRequest(GET, TEST_WORKSPACE_PREFIX + TEST_INDEX_1 + "/_mapping", null))
                .thenReturn(mockResponse);


        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + "/_mapping");

        var request = buildRequest(HttpMethod.GET, pathParams);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldPassBodyToOpenSearch() throws IOException {

        var body = JsonUtils.dtoObjectMapper.readTree("{\"message\": \"test\"}");

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, "{}");
        when(openSearchClient.sendRequest(
                eq(POST),
                eq(TEST_WORKSPACE_PREFIX + TEST_INDEX_1 + "/_mapping"),
                argThat(new JsonStringMatcher(body.toString())))
        ).thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + "/_mapping");

        var request = TestUtils.buildRequestWithBody(HttpMethod.POST, pathParams, body);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldThrowBadRequestWhenGivenIndexBeginningWithUnderscore() throws IOException {

        var pathParams = buildPathParamsForIndex("_someindex");
        var request = buildRequest(HttpMethod.PUT, pathParams);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_BAD_REQUEST)));
    }

    @Test
    void shouldThrowBadRequestWhenUsingNonWhitelistedCharacters() throws IOException {

        var pathParams = buildPathParamsForIndex("some:index");
        var request = buildRequest(HttpMethod.GET, pathParams);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_BAD_REQUEST)));
    }
}
