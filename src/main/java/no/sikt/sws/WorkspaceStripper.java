package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.sikt.sws.models.gateway.Builder;
import no.sikt.sws.models.opensearch.OpenSearchCommand;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JacocoGenerated
public class WorkspaceStripper {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceStripper.class);
    public static final String INDEX = "_index";
    public static final String REQUIRED_PARAMETER_IS_NULL = "required parameter is null -> ";
    public static final String WORKSPACE_PREFIX = "[workspacePrefix]";
    public static final String RESOURCE_IDENTIFIER = "[resourceIdentifier] ";
    public static final String EMPTY_STRING = "";
    public static final String STRIP_WORKSPACE_PATTERN = "-(.+)\"";


    public static JsonNode removePrefix(JsonNode node, String workspacePrefix) {
        if (workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + WORKSPACE_PREFIX);
        }
        if (node == null) {
            return null;
        }

        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

        return string2JsonNode(
            node.toString()
                .replaceAll(regex,EMPTY_STRING));
    }

    // Remove {workspace}- from responseBody but only if beginning of field, word og preceded by '/'
    public static String removePrefix(OpenSearchCommand command, String workspacePrefix, String responseBody)
            throws BadRequestException {
        if (responseBody == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((responseBody == null) ? "[responseBody] " : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }

        //Regex that matches '{workspace}-' preceded by ' ', '/', '[' or '"'
        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

        switch (command) {
            case ALIAS:
            case BULK:
            case MAPPING:
            case OTHER:
            case ERROR:
                return responseBody.replaceAll(regex, EMPTY_STRING);
            case SEARCH:
                return Builder.toJson(Builder.searchFromValues(workspacePrefix,responseBody));
            case DOC:
                return Builder.toJson(Builder.docFromValues(workspacePrefix,responseBody));
            case NOT_IMPLEMENTED:
                throw new BadRequestException("Not implemented");
            case INVALID:
                throw new BadRequestException("resourceIdentifier is invalid");
            default:
                throw new IllegalStateException("Unexpected value: " + command);
        }


    }

    private static String removePrefixOther(String workspacePrefix, String responseBody) {
        return responseBody;
    }


    public static String prefixUrl(String workspacePrefix, String resourceIdentifier) {
        logger.debug("prefixing " + workspacePrefix + resourceIdentifier);
        if (resourceIdentifier == null || resourceIdentifier.isEmpty()) {
            return workspacePrefix + "-*";

        } else if (resourceIdentifier.startsWith("_")) {
            return resourceIdentifier;

        }
        return workspacePrefix + "-" + resourceIdentifier;
    }

    public static String prefixBody(String workspacePrefix, String resourceIdentifier, String gatewayBody)
            throws BadRequestException {

        if (gatewayBody == null) {
            return null;
        }

        if (workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + "workspacePrefix");
        }

        var getEnumt = OpenSearchCommand.fromString(resourceIdentifier);
        logger.debug(getEnumt.name());
        switch (getEnumt) {
            case ALIAS:
                return WorkspaceStripper.prefixAliasBody(workspacePrefix,gatewayBody);
            case BULK:
                return WorkspaceStripper.prefixBulkBody(workspacePrefix,getBulkBody(gatewayBody));
            case SEARCH:
                return WorkspaceStripper.prefixSearchBody(workspacePrefix,gatewayBody);
            case MAPPING:
                return WorkspaceStripper.prefixMappingkBody(workspacePrefix,gatewayBody);
            case DOC:
                logger.info("returning " + workspacePrefix + resourceIdentifier);
                return gatewayBody;
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


    private static String prefixMappingkBody(String workspacePrefix, String gatewayBody) {
        logger.debug(workspacePrefix);
        return gatewayBody;
    }

    private static String prefixSearchBody(String workspacePrefix, String gatewayBody) {
        logger.debug(workspacePrefix);
        return gatewayBody;
    }

    // replace {index} with {workspace}-{index} from responseBody
    public static String prefixIndexesBody(String workspacePrefix, String resourceIdentifier,String gatewayBody) {

        if (gatewayBody == null || resourceIdentifier == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((gatewayBody == null) ? "[gatewayBody] " : EMPTY_STRING)
                    + ((resourceIdentifier == null) ? RESOURCE_IDENTIFIER : EMPTY_STRING)
                    + ((workspacePrefix == null) ? WORKSPACE_PREFIX : EMPTY_STRING));
        }

        var node =  string2JsonNode(gatewayBody);

        if (node.has("aliases")) {
            var aliasesONode = (ObjectNode)node.get("aliases");
            var names = new ArrayList<String>();
            aliasesONode.fieldNames().forEachRemaining(names::add);
            names.forEach(name -> {
                aliasesONode.set(workspacePrefix + "-" + name,aliasesONode.get(name));
                aliasesONode.remove(name);
            });
            ((ObjectNode)node).set("aliases",aliasesONode);
            return node.toPrettyString();
        }
        return gatewayBody;
            //.replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
            //.replaceAll("(\"aliases\".*?\")(.+?\")","$1" + workspacePrefix + "-$2");
    }

    private static String prefixString(String input, String workspacePrefix) {
        return workspacePrefix + "-" + input;
    }

    protected static String prefixBulkBody(String workspacePrefix, List<JsonNode> gatewayBulkBody) {
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
        }).collect(Collectors.joining("\n"))
                + "\n";
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
                .map(WorkspaceStripper::string2JsonNode).collect(Collectors.toList());
    }

    private static JsonNode string2JsonNode(String s) {
        try {
            return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
