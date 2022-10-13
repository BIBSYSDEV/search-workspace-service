package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static no.sikt.sws.constants.ApplicationConstants.REQUIRED_PARAMETER_IS_NULL;

@JacocoGenerated
public class Prefixer {

    private static final Logger logger = LoggerFactory.getLogger(Prefixer.class);
    public static final String INDEX = "_index";

    /**
     * Prefixing of URL.
     * @param workspacePrefix prefix to apply
     * @param resourceIdentifier url to prefix
     * @return prefixed url
     */
    public static String url(String workspacePrefix, String resourceIdentifier) {
        logger.debug("prefixing " + workspacePrefix + resourceIdentifier);
        if (resourceIdentifier == null || resourceIdentifier.isEmpty()) {
            return workspacePrefix + "-*";

        } else if (resourceIdentifier.startsWith("_")) {
            return resourceIdentifier;

        }
        return workspacePrefix + "-" + resourceIdentifier;
    }

    /**
     * Prefixing of body.
     * @param workspacePrefix prefix to apply
     * @param resourceIdentifier url with index name
     * @param gatewayBody body to prefix
     * @return prefixed body
     */
    public static String body(String workspacePrefix, String resourceIdentifier, String gatewayBody)
            throws BadRequestException {

        if (gatewayBody == null) {
            return null;
        }

        if (workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + "workspacePrefix");
        }

        var searchCommand = OpenSearchCommand.fromString(resourceIdentifier);
        logger.debug(searchCommand.name());
        switch (searchCommand) {
            case ALIAS:
                return Prefixer.aliasBody(workspacePrefix,gatewayBody);
            case BULK:
                return Prefixer.bulkBody(workspacePrefix, gatewayBody);
            case SEARCH:
            case MAPPING:
            case DOC:
                logger.debug("Return to sender (untouched) " + workspacePrefix + resourceIdentifier);
                return gatewayBody;
            case OTHER:
                return Prefixer.indexesBody(workspacePrefix,gatewayBody);
            case NOT_IMPLEMENTED:
                throw new BadRequestException("Not implemented " + resourceIdentifier);
            case INVALID:
                throw new BadRequestException("resourceIdentifier [" + resourceIdentifier + "] is invalid");
            default:
                throw new IllegalStateException("Unexpected value: " + resourceIdentifier);
        }
    }

    // replace {index} with {workspace}-{index} from responseBody
    public static String indexesBody(String workspacePrefix,String gatewayBody) {

        var node =  string2JsonNode(gatewayBody);

        nodeUpdated(workspacePrefix,"aliases",node);
        nodeUpdated(workspacePrefix,"settings/index",node);

        return node.toPrettyString();
    }

    private static void nodeUpdated(String workspacePrefix, String nodeName, JsonNode node) {
        if (node.has(nodeName)) {
            var objectNode = (ObjectNode) node.get(nodeName);
            var names = new ArrayList<String>();
            objectNode.fieldNames().forEachRemaining(names::add);
            names.forEach(name -> {
                objectNode.set(workspacePrefix + "-" + name,objectNode.get(name));
                objectNode.remove(name);
            });
            ((ObjectNode) node).set(nodeName,objectNode);
        }
    }

    protected static String bulkBody(String workspacePrefix, String gatewayBulkBody) {

        return convertBulkBodyToJsonList(gatewayBulkBody).stream().map(item -> {
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

        }).collect(Collectors.joining("\n")) + "\n";
    }

    protected static String aliasBody(String workspacePrefix, String gatewayAliasBody) {
        return gatewayAliasBody
                .replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
                .replaceAll("(\"alias\".*?\")(.+?\")","$1" + workspacePrefix + "-$2");
    }

    private static List<JsonNode> convertBulkBodyToJsonList(String bulkBody) {
        return Arrays.stream(bulkBody.split("\n"))
                .map(Prefixer::string2JsonNode).collect(Collectors.toList());
    }

    private static JsonNode string2JsonNode(String s) {
        try {
            return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
