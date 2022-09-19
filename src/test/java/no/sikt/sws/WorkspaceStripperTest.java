package no.sikt.sws;

import junit.framework.TestCase;
import no.sikt.sws.testutils.TestCaseCollection;
import no.sikt.sws.testutils.TestCaseSws;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceStripperTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripperTest.class);

    @Test
    void runBulkRequests() {
        logger.info("Bulk requests");

        new TestCaseCollection("requests-bulk.json")
                .getTestCases()
                .forEach(this::assertTestCase);
    }

    @Test
    void runDocRequests() {
        logger.info("Doc requests");
        new TestCaseCollection("requests-doc.json")
                .getTestCases()
                .forEach(this::assertTestCase);
    }

    @Test
    void runIndexesRequests() {
        logger.info("Index requests");
        new TestCaseCollection("requests-indexes.json")
                .getTestCases()
                .forEach(this::assertTestCase);
    }

    @Test
    void runRootRequests() {
        logger.info("Root requests");
        new TestCaseCollection("requests-root.json")
                .getTestCases()
                .forEach(this::assertTestCase);
    }


    @ParameterizedTest
    @ArgumentsSource( new TestCaseCollection("requests-root.json").getTestCases())
    void assertTestCase(TestCaseSws testCase) {
        logger.info("Asserting "+ testCase.getName());
        logger.info("Expecting" + testCase.getResponseStripped());
        assertEquals(
                testCase.getResponseStripped(),
                WorkspaceStripper.remove(testCase.getResponse(), "test1")
        );
    }
}