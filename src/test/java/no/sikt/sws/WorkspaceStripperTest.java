package no.sikt.sws;

import no.sikt.sws.testutils.TestCaseCollection;
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
        var sbuilder = Stream.<TestCaseSws>builder();

        new TestCaseCollection("request-search.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        new TestCaseCollection("request-search.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        new TestCaseCollection("requests-bulk.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        new TestCaseCollection("requests-doc.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        new TestCaseCollection("requests-indexes.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        new TestCaseCollection("requests-root.json")
            .getElements()
            .forEachRemaining(action ->  sbuilder.add(new TestCaseSws(action)));

        return sbuilder.build();
    }

    @TestFactory
    @DisplayName("AssertEquals")
    Stream<DynamicTest> testStripperFactory() {

        Function<TestCaseSws, String> displayNameGenerator = (input) -> "AssertEqual -> " + input.toString();

        // Executes tests based on the current input value.
        ThrowingConsumer<TestCaseSws> testExecutor = (testCase) -> assertEquals(
            testCase.getResponseStripped(),
            WorkspaceStripper.remove(testCase.getResponse(),WORKSPACENAME));

        // Returns a stream of dynamic tests.
        return DynamicTest.stream(allRequestArguments(), displayNameGenerator, testExecutor);
    }
}