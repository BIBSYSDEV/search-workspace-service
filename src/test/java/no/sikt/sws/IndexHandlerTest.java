package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.models.opensearch.SearchDto;
import no.sikt.sws.testutils.JsonStringMatcher;
import no.sikt.sws.testutils.CaseLoader;
import no.sikt.sws.testutils.CaseSws;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Stream;

import static com.amazonaws.http.HttpMethodName.GET;
import static com.amazonaws.http.HttpMethodName.POST;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_ENTITY_TOO_LARGE;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.Constants.TEST_INDEX_1;
import static no.sikt.sws.testutils.Constants.TEST_PREFIX_MOCKNAME;
import static no.sikt.sws.testutils.Constants.TEST_PREFIX_SONDRE;
import static no.sikt.sws.testutils.Constants.TEST_SCOPE_MOCKNAME;
import static no.sikt.sws.testutils.Constants.TEST_SCOPE_SONDRE;
import static no.sikt.sws.testutils.Utils.buildPathParamsForIndex;
import static no.sikt.sws.testutils.Utils.buildQueryParams;
import static no.sikt.sws.testutils.Utils.buildRequest;
import static no.sikt.sws.testutils.Utils.buildRequestWithBody;
import static no.sikt.sws.testutils.Utils.stripWhitespaceNewLine;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.CloseResource"})
class IndexHandlerTest {

    private static final Context CONTEXT = new FakeContext();
    private static final String MAPPING_PATH = "/_mapping";
    private ByteArrayOutputStream output;

    @InjectMocks
    private final IndexHandler handler = new IndexHandler();

    @Mock
    private OpenSearchClient openSearchClient;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }

    @Test
    void shouldGiveResponse() throws IOException {

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, "{}");

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1 + MAPPING_PATH, null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + MAPPING_PATH);

        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

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
            eq(TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1 + MAPPING_PATH),
            argThat(new JsonStringMatcher(body.toString())))
        ).thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + MAPPING_PATH);

        var request = buildRequestWithBody(HttpMethod.POST, pathParams, body, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldThrowBadRequestWhenGivenIndexBeginningWithUnderscore() throws IOException {

        var pathParams = buildPathParamsForIndex("_someindex");
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_PREFIX_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_BAD_REQUEST)));
    }

    @Test
    void shouldThrowBadRequestWhenUsingNonWhitelistedCharacters() throws IOException {

        var pathParams = buildPathParamsForIndex("some:index");
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_BAD_REQUEST)));
    }

    @Test
    void shouldNotExposeErrorMessageOnInternalErrors() throws IOException {
        var secretNonJsonString = "some invalid json";
        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, secretNonJsonString);

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1, null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1);

        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_INTERNAL_ERROR)));
        assertThat(response.getBody(), not(containsString(secretNonJsonString)));
    }

    @Test
    void shouldPassQueryParams() throws IOException {

        var searchCommand = "_search?param1=1&param2=2";
        var uri = URI.create(TEST_INDEX_1 + "/" + searchCommand);
        final OpenSearchResponse mockResponse =
            new OpenSearchResponse(200, "{\"hits\":{\"total\":{\"value\":0,\"relation\":\"eq\"},\"hits\":[]}}");

        when(openSearchClient.sendRequest(
            GET,
            "workspace-sondre-" + uri,
            null)
        ).thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(uri.toString());
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldReturn413ForTooLargeResponse() throws IOException {
        var longStr = "a".repeat(10_000_000);
        var responseStr = "{ \"indexname\": {\"mappings\":{ \"data\":\"" + longStr + "\"}}}";
        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, responseStr);

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1, null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1);
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var gatewayResponse = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(gatewayResponse.getStatusCode(), is(equalTo(HTTP_ENTITY_TOO_LARGE)));
    }

    @Test
    void shouldReturnShardsFailures() throws IOException {

        var responseStr = "{\"took\":47354,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":4,"
                          + "\"skipped\":0,\"failed\":1,"
                          + "\"failures\":[{\"shard\":3,\"index\":\"" + TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1 + "\","
                          + "\"node\":\"KtsKT2hORLCAKelYrRrX-Q\",\"reason\":"
                          + "{\"type\":\"rejected_execution_exception\","
                          + "\"reason\":\"cancelled task with reason: heap usage exceeded [283.4mb >= 69.2mb]\"}}]}}";
        final OpenSearchResponse mockResponse = new OpenSearchResponse(500, responseStr);

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1 + "/_search", null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + "/_search");
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var gatewayResponse = GatewayResponse.fromOutputStream(output, String.class);
        var json = JsonUtils.dtoObjectMapper.readTree(gatewayResponse.getBody());
        var index = json.get("_shards").get("failures").get(0).get("index").textValue();
        assertThat(gatewayResponse.getStatusCode(), is(equalTo(HTTP_INTERNAL_ERROR)));
        assertThat(index, is(equalTo(TEST_INDEX_1)));
    }

    @Test
    void shouldReturn200ForNotTooLargeResponse() throws IOException {
        var longStr = "a".repeat(1_000_000);
        var responseStr = "{ \"indexname\": {\"mappings\":{ \"data\":\"" + longStr + "\"}}}";
        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, responseStr);

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + "-" + TEST_INDEX_1, null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1);
        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var gatewayResponse = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(gatewayResponse.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldPassSearchScrollDirectlyToOpensearch() throws IOException {

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, "{}");

        when(openSearchClient.sendRequest(GET, "_search/scroll", null))
            .thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex("_search/scroll");

        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @TestFactory
    @DisplayName("Opensearch parameter requests")
    Stream<DynamicTest> testRequestWithQueryParameters() {

        var requestsWithParameters =
            getSearchRequestTestCasesStream().filter(CaseSws::isParamRequestTest);

        assertThat("workaround for PMD.UnitTestShouldIncludeAssert that I could not get working to get through codacy",
                   getSearchRequestTestCasesStream().count(), is(not(equalTo(0))));

        return DynamicTest.stream(
            requestsWithParameters,
            CaseSws::getName,
            this::assertTestCaseWithRequestQueryParameters);
    }

    private void assertTestCaseWithRequestQueryParameters(CaseSws testCase) throws IOException {

        this.output = new ByteArrayOutputStream();
        when(openSearchClient.sendRequest(
            testCase.getRequestOpensearch().getMethod(),
            testCase.getRequestOpensearch().getUrl(),
            testCase.getRequestOpensearch().getBody())
        ).thenReturn(new OpenSearchResponse(200, testCase.getResponse()));

        var gatewayUrl = testCase.getRequestGateway().getUrl().split("\\?", 2);
        var pathParams = buildPathParamsForIndex(gatewayUrl[0]);
        var queryParams = buildQueryParams(gatewayUrl[1]);

        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                          .withHttpMethod(testCase.getRequestGateway().getMethod().name())
                          .withPathParameters(pathParams)
                          .withQueryParameters(queryParams)
                          .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE_MOCKNAME)
                          .build();

        handler.handleRequest(request, output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, String.class);
        var resultBody = SearchDto
                             .fromResponse(response.getBody())
                             .stripper(TEST_PREFIX_MOCKNAME)
                             .toJsonCompact();

        Assertions.assertEquals(
            stripWhitespaceNewLine(testCase.getResponseStripped()),
            stripWhitespaceNewLine(resultBody));

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldHandleSearchRequestWithAggregation() throws IOException {

        var testcase =
            new CaseLoader("proxy/requests-aggregation.json")
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
        var request = buildRequestWithBody(
            HttpMethod.GET,
            pathParams,
            requestGateway.getBody(),
            TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, String.class);
        var resultBody = SearchDto
                             .fromResponse(response.getBody())
                             .stripper(TEST_PREFIX_SONDRE)
                             .toJsonCompact();

        Assertions.assertEquals(
            stripWhitespaceNewLine(testcase.getResponseStripped()),
            stripWhitespaceNewLine(resultBody));

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    private Stream<CaseSws> getSearchRequestTestCasesStream() {
        return new CaseLoader.Builder()
                   .loadResource("proxy/requests-search.json")
                   .build();
    }
}
