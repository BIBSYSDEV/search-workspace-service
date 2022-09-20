package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseLoader;
import no.sikt.sws.testutils.TestCaseSws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceStripperTest {

    private static final String WORKSPACENAME = "test1";

    // Merge all testcases into one stream.
    static Stream<TestCaseSws> allRequestArguments() {
        var streamBuilder = Stream.<TestCaseSws>builder();

        new TestCaseLoader("request-search.json")
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

        return streamBuilder.build();
    }

    @TestFactory
    @DisplayName("AssertEquals")
    Stream<DynamicTest> testStripperFactory() {

        // TestCase in TestName out
        Function<TestCaseSws, String> displayNameGenerator = (input) -> "AssertEqual -> " + input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = (testCase) -> assertEquals(
            testCase.getResponseStripped(),
            WorkspaceStripper.remove(testCase.getResponse(),WORKSPACENAME));

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }
}