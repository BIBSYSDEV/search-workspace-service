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

        // TestCase in, TestName out
        Function<TestCaseSws, String> displayNameGenerator = (input) -> input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertResponseStripping;

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch url prefix-adder")
    Stream<DynamicTest> testPrefixUrlAddingFactory() {

        // TestCase in, TestName out
        Function<TestCaseSws, String> displayNameGenerator = (input) -> input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertUrlPrefixing;

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @TestFactory
    @DisplayName("Opensearch body prefixing")
    Stream<DynamicTest> testPrefixBodyFactory() {

        // TestCase in, TestName out
        Function<TestCaseSws, String> displayNameGenerator = (input) -> input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertBodyPrefixing;

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @Test
    void runRootRequests() {

        new TestCaseLoader("requests-cat.json")
                .getTestCases()
                .forEach(this::assertResponseStripping);
    }

    void assertResponseStripping(TestCaseSws testCase) {
        assertEquals(testCase.getResponseStripped(),WorkspaceStripper.remove(testCase.getResponse(), WORKSPACEPREFIX));
        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }

    void assertUrlPrefixing(TestCaseSws testCase) {
        var gatewayUrl = testCase.getRequestGateway().getUrl();
        var opensearchUrl = testCase.getRequestOpensearch().getUrl();
        assertEquals(opensearchUrl,WorkspaceStripper.prefixUrl(gatewayUrl, WORKSPACEPREFIX));
        logger.info(gatewayUrl + "->" + opensearchUrl);
    }

    void assertBodyPrefixing(TestCaseSws testCase) {
        var index = testCase.getIndexName();
        var gatewayBody = testCase.getRequestGateway().getBody();
        var opensearchBody = testCase.getRequestOpensearch().getBody();

        assertEquals(opensearchBody,WorkspaceStripper.prefixBody(gatewayBody, WORKSPACEPREFIX, index));
        logger.info(testCase.getRequestOpensearch().getMethod() + "->" + testCase.getRequestOpensearch().getUrl());
    }
}