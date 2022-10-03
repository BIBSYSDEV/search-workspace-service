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
    public static final String REQUIRED_PARAMETER_IS_NULL = "required parameter is null -> ";
    public static final String WORKSPACE_PREFIX = "[workspacePrefix]";
    public static final String RESOURCE_IDENTIFIER = "[resourceIdentifier] ";
    public static final String EMPTY_STRING = "";

    // Remove {workspace}- from responseBody but only if beginning of field, word og preceded by '/'
    public static String removePrefix(String workspacePrefix, String responseBody)  {
        if (responseBody == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((responseBody == null) ? "[responseBody] " : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }

        //Regex that matches '{workspace}-' preceded by ' ', '/' or '"'
        var regex = "(?<=[ /\"])" + workspacePrefix + "-";

        return responseBody.replaceAll(regex, EMPTY_STRING);
    }

    public static String prefixUrl(String workspacePrefix, String resourceIdentifier) {
        logger.info("prefixing " + workspacePrefix);
        if (resourceIdentifier == null) {
            return workspacePrefix + "-*";

        } else if (resourceIdentifier.startsWith("_")) {
            return resourceIdentifier;

        }
        return workspacePrefix + "-" + resourceIdentifier;
    }

    public static String prefixBody(String workspacePrefix, String resourceIdentifier, String gatewayBody)
            throws BadRequestException {
        var getEnumt = OpenSearchCommand.fromString(resourceIdentifier);
        logger.info(getEnumt.name());
        switch (getEnumt) {
            case ALIAS:
                return WorkspaceStripper.prefixAliasBody(workspacePrefix,gatewayBody);
            case BULK:
                return WorkspaceStripper.prefixIndexesBulkBody(workspacePrefix,getBulkBody(gatewayBody));
            case OTHER:
                return WorkspaceStripper.prefixIndexesBody(workspacePrefix,resourceIdentifier,gatewayBody);
            case NOT_IMPLEMENTED:
                throw new BadRequestException("Not implemented " + resourceIdentifier);
            case INVALID:
                throw new BadRequestException("resourceIdentifier [" + resourceIdentifier + "] is invalid");
            default:
                throw new IllegalStateException("Unexpected value: " + resourceIdentifier);
        }
    }

    // replace {index} with {workspace}-{index} from responseBody
    public static String prefixIndexesBody(String workspacePrefix, String resourceIdentifier,String gatewayBody) {

        if (gatewayBody == null || resourceIdentifier == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((gatewayBody == null) ? "[gatewayBody] " : EMPTY_STRING)
                    + ((resourceIdentifier == null) ? RESOURCE_IDENTIFIER : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }

        return attempt(() -> {
            var strippedIndex = Arrays.stream(resourceIdentifier.split("/")).findFirst();
            return gatewayBody.replaceAll(resourceIdentifier, workspacePrefix + "-" + strippedIndex);
        }).orElseThrow();
    }


    protected static String prefixIndexesBulkBody(String workspacePrefix, List<JsonNode> gatewayBulkBody) {
        if (gatewayBulkBody == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((gatewayBulkBody == null) ? "[gatewayBulkBody] " : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }
        return gatewayBulkBody.stream().map(item -> {
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

    protected static String prefixAliasBody(String workspacePrefix,String gatewayAliasBody) {
        if (gatewayAliasBody == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((gatewayAliasBody == null) ? "[gatewayAliasBody] " : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }
        return gatewayAliasBody
                .replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
                .replaceAll("(\"alias\".*?\")(.+?\")","$1" + workspacePrefix + "-$2");
    }

    private static List<JsonNode> getBulkBody(String bulkbody) {
        return Arrays.stream(bulkbody.split("\n"))
                .map(s -> {
                    try {
                        return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

}
