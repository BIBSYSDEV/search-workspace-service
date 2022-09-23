package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import org.joda.time.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceStripperTest {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripperTest.class);
    private static final String WORKSPACEPREFIX = "workspace-mockname";

    // Merge all testcases into one stream.
    static Stream<TestCaseSws> allRequestArguments() {
        final ReadableInstant before = new Instant();
        logger.info("Test cases loading");
        var streamBuilder = Stream.<TestCaseSws>builder();

        new TestCaseLoader("requests-mapping.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        new TestCaseLoader("requests-search.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        new TestCaseLoader("requests-bulk.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        new TestCaseLoader("requests-doc.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        new TestCaseLoader("requests-indexes.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        new TestCaseLoader("requests-cat.json")
                .getTestCases()
                .forEach(streamBuilder::add);

        logger.info("loaded -> {} ms.", new Period(before,new Instant()).getMillis());
        return streamBuilder.build();
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
    @Test
    void runSingleTestcase() {
        var filename = "requests-bulk.json";
        var testName = "Bulk POST create index with evil stupid names";

        var testCase = new TestCaseLoader(filename)
                .getTestCase(testName);

        assertBulkBodyPrefixing(testCase);

    }

    void assertResponseStripping(TestCaseSws testCase) {
        var expectedResponse = testCase.getResponseStripped();
        var resultResponse = WorkspaceStripper.remove(testCase.getResponse(), WORKSPACEPREFIX);

        assertEquals(expectedResponse,resultResponse);

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }

    void assertUrlPrefixing(TestCaseSws testCase) {
        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var expectedUrl = testCase.getRequestOpensearch().getUrl();
        var resultUrl = WorkspaceStripper.prefixUrl(gatewayUrl, WORKSPACEPREFIX);

        assertEquals(expectedUrl,resultUrl);

        logger.info(gatewayUrl + "->" + expectedUrl);
    }

    void assertBodyPrefixing(TestCaseSws testCase) {
        var indexName = testCase.getIndexName();
        var gatewayBody = testCase.getRequestGateway().getBody();
        var expectedBody = testCase.getRequestOpensearch().getBody();
        var resultBody = WorkspaceStripper.prefixBody(gatewayBody, WORKSPACEPREFIX, indexName);

        assertEquals(expectedBody,resultBody);

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }

    void assertBulkBodyPrefixing(TestCaseSws testCase) {
        var gatewayBody = testCase.getRequestGateway().getBulkBody();
        var expectedBody = testCase.getRequestOpensearch().getBody();
        var resultBody = WorkspaceStripper.prefixBody(gatewayBody, WORKSPACEPREFIX);

        assertEquals(expectedBody,resultBody);

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }

}