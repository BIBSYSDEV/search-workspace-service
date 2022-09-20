package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import org.joda.time.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
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
        ReadableInstant before = new Instant();
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

        new TestCaseLoader("requests-root.json")
            .getElements()
            .forEachRemaining(argument ->  streamBuilder.add(new TestCaseSws(argument)));

        logger.info("loaded -> {} ms.", new Period(before,new Instant()).getMillis());
        return streamBuilder.build();
    }

    @TestFactory
    @DisplayName("AssertEquals")
    Stream<DynamicTest> testStripperFactory() {

        // TestCase in, TestName out
        Function<TestCaseSws, String> displayNameGenerator = (input) -> "AssertEqual -> " + input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = this::assertTestCase;

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }

    @Test
    void runRootRequests() {

        new TestCaseLoader("requests-root.json")
                .getTestCases()
                .forEach(this::assertTestCase);
    }

    void assertTestCase(TestCaseSws testCase) {
        assertEquals(testCase.getResponseStripped(),WorkspaceStripper.remove(testCase.getResponse(), WORKSPACEPREFIX));
        logger.info(testCase.getRequest().getMethod() + "->" + testCase.getRequest().getUrl());

    }
}