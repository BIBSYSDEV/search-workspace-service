package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.models.OpenSearchCommand;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;
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
    public static final String INDEX = "_index";

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
        logger.info("prefixing " + workspace);
        if (index == null) {
            return workspace + "-*";

        } else if (index.startsWith("_")) {
            return index;

        }
        return workspace + "-" + index;
    }

    public static String prefixBody(String resourceIdentifier, String workspace, String body)
            throws BadRequestException, IllegalStateException {
        var getEnumt = OpenSearchCommand.fromString(resourceIdentifier);

        switch (getEnumt) {
            case ALIAS:
                return WorkspaceStripper.prefixAliasBody(body,workspace);
            case BULK:
                return WorkspaceStripper.prefixIndexesBulkBody(getBulkBody(body),workspace);
            case OTHER:
                return body;
            case NOT_IMPLEMENTED:
                throw new BadRequestException("Not implemented " + resourceIdentifier);
            case INVALID:
                throw new BadRequestException("resourceIdentifier [" + resourceIdentifier + "] is invalid");
            default:
                throw new IllegalStateException("Unexpected value: " + resourceIdentifier);
        }
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


    protected static String prefixIndexesBulkBody(List<JsonNode> gatewayBody, String workspacePrefix) {
        if (gatewayBody == null || workspacePrefix == null) {
            return null;
        }
        return gatewayBody.stream().map(item -> {
            String indexName;
            if (item.has("index")) {
                indexName = item.get("index").get(INDEX).textValue();
            } else if (item.has("delete")) {
                indexName = item.get("delete").get(INDEX).textValue();
            } else if (item.has("update")) {
                indexName = item.get("update").get(INDEX).textValue();
            } else if (item.has("create")) {
                indexName = item.get("create").get(INDEX).textValue();
            } else {
                return item.toString();
            }
            var workspaceIndexName = String.format("\"%s-%s\"", workspacePrefix, indexName);
            indexName = String.format("\"%s\"",  indexName);

            return item.toString().replaceAll(indexName,workspaceIndexName);
        }).collect(Collectors.joining("\n"));
    }

    protected static String prefixAliasBody(String aliasBody, String workspacePrefix) {
        if (aliasBody == null || workspacePrefix == null) {
            return null;
        }
        return aliasBody
                .replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
                .replaceAll("(\"alias\".*?\")(.+?\")","$1" + workspacePrefix + "-$2");
    }

    private static List<JsonNode> getBulkBody(String body) {
        return Arrays.stream(body.split("\n"))
                .map(s -> {
                    try {
                        return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

}
