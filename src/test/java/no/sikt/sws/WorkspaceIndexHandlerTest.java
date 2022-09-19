package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonNode;
import junit.framework.TestCase;
import no.sikt.sws.testutils.JsonStringMatcher;
import no.sikt.sws.testutils.TestCaseSws;
import no.sikt.sws.testutils.TestCaseCollection;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.amazonaws.http.HttpMethodName.POST;
import static com.amazonaws.http.HttpMethodName.PUT;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.WorkspaceIndexHandler.RESOURCE_IDENTIFIER;
import static no.sikt.sws.testutils.TestConstants.*;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class WorkspaceIndexHandlerTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceIndexHandlerTest.class);

    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;

    @InjectMocks
    WorkspaceIndexHandler handler = new WorkspaceIndexHandler();

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

        when(openSearchClient.sendRequest(PUT, TEST_WORKSPACE_PREFIX + TEST_INDEX_1 + "/_mapping", null))
                .thenReturn(mockResponse);


        var pathparams = Map.of(
                RESOURCE_IDENTIFIER, TEST_INDEX_1 + "/_mapping"
        );

        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(HttpMethod.PUT.toString())
                .withPathParameters(pathparams)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .build();

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


        var pathparams = Map.of(
                RESOURCE_IDENTIFIER, TEST_INDEX_1 + "/_mapping"
        );

        var request = new HandlerRequestBuilder<JsonNode>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(HttpMethod.POST.toString())
                .withPathParameters(pathparams)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .withBody(body)
                .build();

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void runBulkRequests() {
        logger.info("Bulk requests");

        new TestCaseCollection("requests-bulk.json")
                .getTestCases()
                .forEach(this::shouldGiveResponseTestCase);
    }

    @Test
    void runDocRequests() {
        logger.info("Doc requests");
        new TestCaseCollection("requests-doc.json")
                .getTestCases()
                .forEach(this::shouldGiveResponseTestCase);
    }

    @Test
    void runIndexesRequests() {
        logger.info("Index requests");
        new TestCaseCollection("requests-indexes.json")
                .getTestCases()
                .forEach(this::shouldGiveResponseTestCase);
    }

    @Test
    void runRootRequests() {
        logger.info("Root requests");
        new TestCaseCollection("requests-root.json")
                .getTestCases()
                .forEach(this::shouldGiveResponseTestCase);
    }


    void shouldGiveResponseTestCase(TestCaseSws testCase) {
        assertEquals(
                testCase.getResponseStripped(),
                WorkspaceStripper.remove(testCase.getResponse(), "test1")
        );

        /*
        try {

            logger.info("Test -> '" + testCase.getName() + "'");

            final OpenSearchResponse mockResponse = new OpenSearchResponse(200, "{}");

            when(openSearchClient.sendRequest(
                    eq(testCase.getRequest().getMethod()),
                    eq(TEST_WORKSPACE_PREFIX + testCase.getRequest().getUrl()),
                    argThat(new JsonStringMatcher(testCase.getRequest().getBody())))
            ).thenReturn(mockResponse);

//            when(openSearchClient.sendRequest(
//                    eq(POST),
//                    eq(TEST_WORKSPACE_PREFIX + TEST_INDEX_1 + "/_mapping"),
//                    argThat(new JsonStringMatcher(body.toString())))
//            ).thenReturn(mockResponse);


            var pathparams = Map.of(
                    RESOURCE_IDENTIFIER, testCase.getRequest().getUrl()
            );

            var request = new HandlerRequestBuilder<JsonNode>(JsonUtils.dtoObjectMapper)
                    .withHttpMethod(testCase.getRequest().getMethod().toString())
                    .withPathParameters(pathparams)
                    .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                    .withBody(testCase.getRequest().getBodyNode())
                    .build();

            handler.handleRequest(request, output, CONTEXT);
            var response = GatewayResponse.fromOutputStream(output, String.class);

            new JsonStringMatcher(response.getBody()).matches(testCase.getResponseStripped());

            assertThat(response.getBody(), is(equalTo(HTTP_OK)));

        } catch (Exception e) {
            logger.error("Ignoring error and continuing with next test");
        }

         */
    }
}