package no.sikt.sws;

import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class WorkspaceStripper {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripper.class);

    // Remove {workspace}- from responseBody
    public static String remove(String body, String workspace) {
        if (body == null) {
            return null;
        }
        logger.info("removing " + workspace);
        return body.replaceAll(workspace + "-","");
    }

    public static String prefixUrl(String index, String workspace) {
        if (index == null) {
            return null;
        }
        logger.info("prefixing " + workspace);
        return workspace + "-" + index;
    }

    // replace {index} with {workspace}-{index} from responseBody
    public static String prefixBody(String body, String workspace, String index) {
        if (body == null || index == null) {
            return null;
        }

        return attempt(() -> {
            var strippedIndex = Arrays.stream(index.split("/")).findFirst();
            return body.replaceAll(index, workspace + "-" + strippedIndex);
        }).orElseThrow();
    }
}
