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
            index = "*";
        } else if (index.startsWith("_")) {
            return index;
        }
        logger.info("prefixing " + workspace);
        return workspace + "-" + index;
    }

    // replace {index} with {workspace}-{index} from responseBody
    public static String prefixIndexesBody(String body, String workspace, String index) {
        if (body == null || index == null || workspace == null) {
            return null;
        }

        return attempt(() -> {
            var strippedIndex = Arrays.stream(index.split("/")).findFirst();
            return body.replaceAll(index, workspace + "-" + strippedIndex);
        }).orElseThrow();
    }


    public static String prefixIndexesBody(List<JsonNode> gatewayBody, String workspacePrefix) {
        if (gatewayBody == null || workspacePrefix == null) {
            return null;
        }
        return gatewayBody.stream().map(item -> {
            String indexName;
            if (item.has("index")) {
                indexName = item.get("index").get("_index").textValue();
            } else if (item.has("delete")) {
                indexName = item.get("delete").get("_index").textValue();
            } else if (item.has("update")) {
                indexName = item.get("update").get("_index").textValue();
            } else if (item.has("create")) {
                indexName = item.get("create").get("_index").textValue();
            } else {
                return item.toString();
            }
            var workspaceIndexName = String.format("\"%s-%s\"", workspacePrefix, indexName);
            indexName = String.format("\"%s\"",  indexName);

            return item.toString().replaceAll(indexName,workspaceIndexName);
        }).collect(Collectors.joining("\n"));
    }


}
