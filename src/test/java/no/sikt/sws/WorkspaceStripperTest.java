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
    Stream<DynamicTest> testStripperFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString;  // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertResponseStripping;  // -> test function
        var testCases = allRequestArguments().filter(TestCaseSws::isResponseTest);

        return DynamicTest.stream(testCases, displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch url prefixing")
    Stream<DynamicTest> testPrefixUrlAddingFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertUrlPrefixing;      // -> test function
        var testCases = allRequestArguments().filter(TestCaseSws::isRequestTest);

        return DynamicTest.stream(testCases, displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch request-body prefixing")
    Stream<DynamicTest> testPrefixBodyFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertBodyPrefixing;     // -> test function
        var testCases = allRequestArguments().filter(TestCaseSws::isRequestBodyTest);

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
            assertBodyPrefixing(testCase);
            assertUrlPrefixing(testCase);
        }

        if (testCase.isResponseTest()) {
            assertResponseStripping(testCase);
        }

    }

    @Test
    void assertAliasPrefixing() {
        var filename = "requests-alias.json";

        new TestCaseLoader(filename)
            .getTestCases().forEach(this::assertBodyPrefixAlias);
    }



    void assertResponseStripping(TestCaseSws testCase) {
        logger.info(testCase.getName());
        Assumptions.assumeTrue(testCase.isEnabled());

        var expectedResponse = testCase.getResponseStripped();
        var openSearchResponse = testCase.getResponse();
        var resultResponse = WorkspaceStripper.removePrefix(WORKSPACEPREFIX,openSearchResponse);

        assertEquals(expectedResponse,resultResponse);

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
        logger.info(resultResponse);
    }

    void assertUrlPrefixing(TestCaseSws testCase) {
        logger.info(testCase.getName());
        Assumptions.assumeTrue(testCase.isEnabled());

        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var expectedUrl = testCase.getRequestOpensearch().getUrl();
        var resultUrl = WorkspaceStripper.prefixUrl(WORKSPACEPREFIX,gatewayUrl);

        logger.info(gatewayUrl + "->" + expectedUrl);

        assertEquals(expectedUrl,resultUrl);
    }

    void assertBodyPrefixAlias(TestCaseSws testCase) {
        logger.info(testCase.getName());
        Assumptions.assumeTrue(testCase.isEnabled());

        var expectedBody = testCase.getRequestOpensearch().getBody();
        var gatewayBody = testCase.getRequestGateway().getBody();
        var indexName = testCase.getRequestGateway().getUrl();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX,indexName,gatewayBody));

        assertEquals(expectedBody,resultBody.get());
        logger.info(resultBody.get());
    }

    void assertBodyPrefixing(TestCaseSws testCase) {
        logger.info(testCase.getName());
        Assumptions.assumeTrue(testCase.isEnabled());

        var indexName = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();

        var gatewayBody = testCase.getRequestGateway().getBody();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX, indexName, gatewayBody));

        assertEquals(expectedBody,resultBody.get());

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());

    }

}