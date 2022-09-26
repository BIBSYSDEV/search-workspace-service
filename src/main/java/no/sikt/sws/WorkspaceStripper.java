package no.sikt.sws;

import com.fasterxml.jackson.databind.JsonNode;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class WorkspaceStripper {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripper.class);

    // Remove {workspace}- from responseBody but only if beginning of field, word og preceded by '/'
    public static String remove(String body, String workspace) {
        if (body == null) {
            return null;
        }

        //Regex that matches '{workspace}-' preceded by ' ', '/' or '"'
        var regex = "(?<=[ /\"])" + workspace + "-";

        return body.replaceAll(regex, "");
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


    public static String prefixBody(List<JsonNode> gatewayBody, String workspaceprefix) {
        return gatewayBody.stream().map(item -> {
            // må lage nye returverdier...
            // logger.info(item.toPrettyString());
            item.elements().forEachRemaining(node -> {
                //if (node.has("_index")) {
                //node.get("_index") = node.
                //}
                logger.info(node.toString());
                logger.info(node.asToken().asString());
            });
            return item.get("_index").toPrettyString();
        }).collect(Collectors.joining());

    }
}
