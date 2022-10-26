package no.sikt.sws;

import no.sikt.sws.models.opensearch.OpenSearchCommandKind;
import no.sikt.sws.models.opensearch.OpenSearchResponseKind;
import no.sikt.sws.models.internal.WorkspaceResponse;
import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import nva.commons.apigateway.exceptions.BadRequestException;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.unit.nva.testutils.RandomDataGenerator.objectMapper;
import static nva.commons.core.attempt.Try.attempt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrefixStripperTest {

    private static final Logger logger = LoggerFactory.getLogger(PrefixStripperTest.class);
    static final String WORKSPACEPREFIX = "workspace-mockname";

    /**
    **  Merge all testcases into one stream.
    **/
    static Stream<TestCaseSws> allRequestArguments() {
        final ReadableInstant before = new Instant();
        logger.info("Test cases loading");
        var streamBuilder = Stream.<TestCaseSws>builder();

        loadTestCases(streamBuilder, "proxy/requests-mapping.json");
        loadTestCases(streamBuilder, "proxy/requests-search.json");
        loadTestCases(streamBuilder, "proxy/requests-bulk.json");
        loadTestCases(streamBuilder, "proxy/requests-doc.json");
        loadTestCases(streamBuilder, "proxy/requests-indexes.json");
        loadTestCases(streamBuilder, "proxy/requests-alias.json");

        logger.info("loaded -> {} ms.", new Period(before,new Instant()).getMillis());
        return streamBuilder.build();
    }

    @TestFactory
    @DisplayName("Opensearch url prefixing")
    Stream<DynamicTest> testUrlPrefixFactory() {

        var requestTests = allRequestArguments().filter(TestCaseSws::isRequestTest);

        return DynamicTest.stream(requestTests, TestCaseSws::getName, this::assertUrlPrefix);
    }

    @TestFactory
    @DisplayName("Opensearch request-body prefixing")
    Stream<DynamicTest> testRequestPrefixFactory() {

        var requestBodyTests = allRequestArguments().filter(TestCaseSws::isRequestBodyTest);

        return DynamicTest.stream(requestBodyTests, TestCaseSws::getName, this::assertRequestPrefix);
    }

    @TestFactory
    @DisplayName("Opensearch response-body stripping")
    Stream<DynamicTest> testResponseStrippingFactory() {

        var responseTests = allRequestArguments().filter(TestCaseSws::isResponseTest);

        return DynamicTest.stream(responseTests, TestCaseSws::getName, this::assertResponseStripping);
    }

    @Test
    @DisplayName("Gateway response workspace stripping")
    void testResponseIndexStripping() {
        var filename = "workspace/response-workspace.json";
        var testName = "GET (all) indexes (workspace / )";

        var testCase = new TestCaseLoader(filename)
            .getTestCase(testName);

        if (testCase.isIndexResponse()) {
            assertResponseIndexStripping(testCase);
        }

    }

    /**
     * Using for debuging a single test-case. Change the filename, testcase,
     * and assert-function on the last line as needed
     */
    @Test
    @Disabled
    void runSingleTestcase() throws BadRequestException {
        var filename = "proxy/requests-indexes.json";
        var testName = "PUT index by name, with template";

        var testCase = new TestCaseLoader(filename)
            .getTestCase(testName);

        if (testCase.isRequestBodyTest()) {
            assertRequestPrefix(testCase);
            assertUrlPrefix(testCase);
        }

        if (testCase.isResponseTest()) {
            assertResponseStripping(testCase);
        }

    }

    @Test
    @DisplayName("Opensearch request-body-alias prefixing")
    void assertRequestPrefixAlias() {
        var filename = "proxy/requests-alias.json";
        new TestCaseLoader(filename)
            .getTestCases()
            .forEach(this::assertAliasPrefix);
    }

    void assertResponseIndexStripping(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
            + " http://apigateway/ -> " + testCase.getRequestGateway().getUrl()
            + " http://opensearch/" +  testCase.getRequestOpensearch().getUrl());

        var expectedResponse = testCase.getResponseStripped();

        var workspaceResponse = attempt(() ->
            WorkspaceResponse.fromValues(WORKSPACEPREFIX,testCase.getResponse())).get();

        var resultResponse = attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(workspaceResponse.indexList)).get();

        assertEquals(expectedResponse,resultResponse);
    }

    void assertResponseStripping(TestCaseSws testCase) throws BadRequestException {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
            + " http://opensearch/" +  testCase.getRequestOpensearch().getUrl());

        var expectedResponse = testCase.getResponseStripped();
        var openSearchResponse = testCase.getResponse();
        var httpMethod = testCase.getRequestOpensearch().getMethod();
        var command =
            OpenSearchCommandKind.fromString(testCase.getRequestGateway().getUrl());
        var responseKind =
            OpenSearchResponseKind.fromString(httpMethod,command,openSearchResponse);

        logger.info("--> " + command.name());
        logger.info("<-- " + responseKind.name());
        var resultResponse = PrefixStripper.body(command, responseKind, WORKSPACEPREFIX,openSearchResponse);

        assertEquals(toJsonCompact(expectedResponse),toJsonCompact(resultResponse));
    }

    void assertUrlPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var expectedUrl = testCase.getRequestOpensearch().getUrl();
        var resultUrl = Prefixer.url(WORKSPACEPREFIX,gatewayUrl);

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
                + " http://opensearch/" + resultUrl);

        assertEquals(expectedUrl,resultUrl);

    }

    void assertAliasPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        var resourceIdentifier = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();

        var gatewayBody = testCase.getRequestGateway().getBody();
        var resultBody = attempt(() ->
            Prefixer.body(WORKSPACEPREFIX,resourceIdentifier,gatewayBody)).get();

        assertEquals(expectedBody,resultBody);
    }

    /**
     * Testing body prefixing gateway request to opensearch requests.
     */
    void assertRequestPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
            + " http://opensearch/" + testCase.getRequestOpensearch().getUrl());

        var resourceIdentifier = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();
        var gatewayBody = testCase.getRequestGateway().getBody();
        var command =
            OpenSearchCommandKind.fromString(testCase.getRequestGateway().getUrl());

        logger.info("--> " + command.name());

        var resultBody = attempt(() ->
            Prefixer.body(WORKSPACEPREFIX, resourceIdentifier, gatewayBody)
        ).get();

        assertEquals(toJsonCompact(expectedBody),toJsonCompact(resultBody));
    }

    private static void loadTestCases(Stream.Builder<TestCaseSws> streamBuilder, String filename) {
        new TestCaseLoader(filename)
            .getTestCases()
            .forEach(streamBuilder::add);
    }

    private String toJsonCompact(String body) {
        return body.replaceAll("[\n\r ]", EMPTY_STRING);
    }

}
