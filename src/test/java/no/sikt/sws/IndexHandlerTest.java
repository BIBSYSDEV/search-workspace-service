package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.sikt.sws.models.opensearch.SearchDto;
import no.sikt.sws.testutils.JsonStringMatcher;
import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import no.sikt.sws.testutils.TestUtils;
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
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestConstants.TEST_INDEX_1;
import static no.sikt.sws.testutils.TestConstants.TEST_PREFIX_MOCKNAME;
import static no.sikt.sws.testutils.TestConstants.TEST_PREFIX_SONDRE;
import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE_MOCKNAME;
import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE_SONDRE;
import static no.sikt.sws.testutils.TestUtils.*;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + TEST_INDEX_1 + "/_mapping", null))
                .thenReturn(mockResponse);


        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + "/_mapping");

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
                eq(TEST_PREFIX_SONDRE + TEST_INDEX_1 + "/_mapping"),
                argThat(new JsonStringMatcher(body.toString())))
        ).thenReturn(mockResponse);

        var pathParams = buildPathParamsForIndex(TEST_INDEX_1 + "/_mapping");

        var request = TestUtils.buildRequestWithBody(HttpMethod.POST, pathParams, body, TEST_SCOPE_SONDRE);

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

        when(openSearchClient.sendRequest(GET, TEST_PREFIX_SONDRE + TEST_INDEX_1, null))
            .thenReturn(mockResponse);


        var pathParams = buildPathParamsForIndex(TEST_INDEX_1);

        var request = buildRequest(HttpMethod.GET, pathParams, TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, String.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_INTERNAL_ERROR)));
        assertThat(response.getBody(), not(containsString(secretNonJsonString)));
    }

    @Test
    void shuldPassQueryParams() throws IOException {

        var searchCommand = "_search?param1=1&param2=2";
        var uri = URI.create(TEST_INDEX_1 + "/" + searchCommand);
        final OpenSearchResponse mockResponse =
            new OpenSearchResponse(200,"{\"hits\":{\"total\":{\"value\":0,\"relation\":\"eq\"},\"hits\":[]}}");

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

    @TestFactory
    @DisplayName("Opensearch parameter requests")
    @SuppressWarnings("JUnitMalformedDeclaration")
    public Stream<DynamicTest> testRequestWithQueryParameters() {

        var requestsWithParameters =
            getSearchRequestTestCasesStream().filter(TestCaseSws::isParamRequestTest);

        return DynamicTest.stream(
            requestsWithParameters,
            TestCaseSws::getName,
            this::assertTestCaseWithRequestQueryParameters);
    }

    void assertTestCaseWithRequestQueryParameters(TestCaseSws testCase) throws IOException {

        this.output = new ByteArrayOutputStream();
        when(openSearchClient.sendRequest(
                testCase.getRequestOpensearch().getMethod(),
                testCase.getRequestOpensearch().getUrl(),
                testCase.getRequestOpensearch().getBody())
        ).thenReturn(new OpenSearchResponse(200, testCase.getResponse()));

        var gatewayUrl = testCase.getRequestGateway().getUrl().split("\\?",2);
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
        var resultBody =  SearchDto
                .fromResponse(response.getBody())
                .stripper(TEST_PREFIX_MOCKNAME)
                .toJsonCompact();

        Assertions.assertEquals(readCompact(testCase.getResponseStripped()), readCompact(resultBody));

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    @Test
    void shouldHandleSearchRequestWithAggregation() throws IOException {

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
        var request = buildRequestWithBody(
                HttpMethod.GET,
                pathParams,
                requestGateway.getBody(),
                TEST_SCOPE_SONDRE);

        handler.handleRequest(request, output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, String.class);
        var resultBody =  SearchDto
                .fromResponse(response.getBody())
                .stripper(TEST_PREFIX_SONDRE)
                .toJsonCompact();

        Assertions.assertEquals(readCompact(testcase.getResponseStripped()), readCompact(resultBody));

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

    Stream<TestCaseSws> getSearchRequestTestCasesStream() {
        return new TestCaseLoader.Builder()
            .loadResource("proxy/requests-search.json")
            .build();
    }

}
