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

        new TestCaseLoader("request-mapping.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        new TestCaseLoader("request-search.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        new TestCaseLoader("requests-bulk.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        new TestCaseLoader("requests-doc.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        new TestCaseLoader("requests-indexes.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        new TestCaseLoader("requests-cat.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        logger.info("loaded -> {} ms.", new Period(before,new Instant()).getMillis());
        return streamBuilder.build();
    }

    @TestFactory
    @DisplayName("Opensearch body stripping")
    Stream<DynamicTest> testStripperFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString;  // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertResponseStripping;  // -> test function

        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch url prefix-adder")
    Stream<DynamicTest> testPrefixUrlAddingFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertUrlPrefixing;      // -> test function

        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch body prefixing")
    Stream<DynamicTest> testPrefixBodyFactory() {
        Function<TestCaseSws, String> displayNameGenerator = TestCaseSws::toString; // -> testcase name
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertBodyPrefixing;     // -> test function

        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
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

    //@Test
    //void runRootRequests() {
    //
    //    new TestCaseLoader("requests-cat.json")
    //            .getTestCases()
    //            .forEach(this::assertResponseStripping);
    //}
}