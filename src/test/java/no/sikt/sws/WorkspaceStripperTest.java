package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.stream.Stream;

import static nva.commons.core.attempt.Try.attempt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceStripperTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripperTest.class);
    private static final String WORKSPACEPREFIX = "workspace-mockname";

    // Merge all testcases into one stream.
    static Stream<TestCaseSws> allRequestArguments() {
        final ReadableInstant before = new Instant();
        logger.info("Test cases loading");
        var streamBuilder = Stream.<TestCaseSws>builder();

        loadTestCases(streamBuilder, "requests-mapping.json");
        loadTestCases(streamBuilder, "requests-search.json");
        loadTestCases(streamBuilder, "requests-bulk.json");
        loadTestCases(streamBuilder, "requests-doc.json");
        loadTestCases(streamBuilder, "requests-indexes.json");
        loadTestCases(streamBuilder, "requests-cat.json");

        logger.info("loaded -> {} ms.", new Period(before,new Instant()).getMillis());
        return streamBuilder.build();
    }

    private static void loadTestCases(Stream.Builder<TestCaseSws> streamBuilder, String filename) {
        new TestCaseLoader(filename)
                .getTestCases()
                .forEach(streamBuilder::add);
    }

    @TestFactory
    @DisplayName("Opensearch response-body stripping")
    Stream<DynamicTest> testResponseStrippingFactory() {
        var testCases = allRequestArguments().filter(TestCaseSws::isResponseTest);
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::getName;  // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertResponseStripping;  // -> test function

        return DynamicTest.stream(testCases, displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch url prefixing")
    Stream<DynamicTest> testUrlPrefixFactory() {
        var testCases = allRequestArguments().filter(TestCaseSws::isRequestTest);
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::getName; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertUrlPrefix;      // -> test function

        return DynamicTest.stream(testCases, displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch request-body prefixing")
    Stream<DynamicTest> testRequestPrefixFactory() {
        var testCases = allRequestArguments().filter(TestCaseSws::isRequestBodyTest);
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::getName; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertRequestPrefix;     // -> test function

        return DynamicTest.stream(testCases, displayNameGenerator, testExecutor);
    }

    /**
     * Using for debuging a single test-case. Change the filename, testcase,
     * and assert-function on the last line as needed
     */
    @Disabled
    @Test
    void runSingleTestcase() {
        var filename = "requests-indexes.json";
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
        var filename = "requests-alias.json";
        new TestCaseLoader(filename)
            .getTestCases()
            .forEach(this::assertAliasPrefix);
    }


    void assertResponseStripping(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
            + " http://opensearch/" +  testCase.getRequestOpensearch().getUrl());

        var expectedResponse = testCase.getResponseStripped();
        var openSearchResponse = testCase.getResponse();
        var resultResponse = WorkspaceStripper.removePrefix(WORKSPACEPREFIX,openSearchResponse);

        assertEquals(expectedResponse,resultResponse);
    }

    void assertUrlPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var expectedUrl = testCase.getRequestOpensearch().getUrl();
        var resultUrl = WorkspaceStripper.prefixUrl(WORKSPACEPREFIX,gatewayUrl);

        assertEquals(expectedUrl,resultUrl);

    }

    void assertAliasPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        var indexName = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();

        var gatewayBody = testCase.getRequestGateway().getBody();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX,indexName,gatewayBody));

        assertEquals(expectedBody,resultBody.get());
    }

    void assertRequestPrefix(TestCaseSws testCase) {
        logger.info(testCase.toString());
        Assumptions.assumeTrue(testCase.isEnabled());

        logger.info("--> " + testCase.getRequestOpensearch().getMethod()
            + " http://opensearch/" + testCase.getRequestOpensearch().getUrl());

        var indexName = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();

        var gatewayBody = testCase.getRequestGateway().getBody();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX, indexName, gatewayBody));

        assertEquals(expectedBody,resultBody.get());
    }

}