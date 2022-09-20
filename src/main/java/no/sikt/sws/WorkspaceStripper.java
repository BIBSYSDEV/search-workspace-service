package no.sikt.sws;

import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // replace {index} with {workspace}-{index} from responseBody
    //public static String add(String body, String workspace, String index) {
    //    if (body == null) {
    //        return null;
    //    }
    //
    //    return attempt(() -> {
    //        var strippedIndex = Arrays.stream(index.split("/")).findFirst();
    //        return body.replaceAll(index, workspace + "-" + strippedIndex);
    //    }).orElseThrow();
    //}
}
