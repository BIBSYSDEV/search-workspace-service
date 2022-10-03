package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
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
        var filename = "requests-indexes.json";
        var testName = "PUT index, without template but with stupid name";

        var testCase = new TestCaseLoader(filename)
                .getTestCase(testName);

        // assertBulkBodyPrefixing(testCase);
        assertUrlPrefixing(testCase);
        assertResponseStripping(testCase);

    }

    @Test
    void assertAliasPrefixing() {
        var filename = "requests-alias.json";

        new TestCaseLoader(filename)
            .getTestCases().forEach(this::assertBodyPrefixAlias);
    }



    void assertResponseStripping(TestCaseSws testCase) {
        var expectedResponse = testCase.getResponseStripped();
        var openSearchResponse = testCase.getResponse();
        var resultResponse = WorkspaceStripper.removePrefix(WORKSPACEPREFIX,openSearchResponse);

        assertEquals(expectedResponse,resultResponse);

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
        logger.info(resultResponse);
    }

    void assertUrlPrefixing(TestCaseSws testCase) {
        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var expectedUrl = testCase.getRequestOpensearch().getUrl();
        var resultUrl = WorkspaceStripper.prefixUrl(WORKSPACEPREFIX,gatewayUrl);

        logger.info(gatewayUrl + "->" + expectedUrl);

        assertEquals(expectedUrl,resultUrl);

    }

    void assertBodyPrefixAlias(TestCaseSws testCase) {
        var expectedBody = testCase.getRequestOpensearch().getBody();
        var gatewayBody = testCase.getRequestGateway().getBody();
        var indexName = testCase.getRequestGateway().getUrl();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX,indexName,gatewayBody));

        assertEquals(expectedBody,resultBody.get());
        logger.info(resultBody.get());

    }

    void assertBodyPrefixing(TestCaseSws testCase) {
        var indexName = testCase.getRequestGateway().getUrl();
        var expectedBody = testCase.getRequestOpensearch().getBody();

        var gatewayBody = testCase.getRequestGateway().getBody();
        var resultBody = attempt(() -> WorkspaceStripper.prefixBody(WORKSPACEPREFIX, indexName, gatewayBody));

        assertEquals(expectedBody,resultBody.get());

        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }

}