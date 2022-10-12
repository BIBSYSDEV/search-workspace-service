package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.models.gateway.Builder;
import no.sikt.sws.models.opensearch.OpenSearchCommand;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;
import nva.commons.core.JacocoGenerated;

@JacocoGenerated
public class ResponseStripper {

    public static final String REQUIRED_PARAMETER_IS_NULL = "required parameter is null -> ";
    public static final String WORKSPACE_PREFIX = "[workspacePrefix]";
    public static final String EMPTY_STRING = "";


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
                return responseBody.replaceAll(regex, EMPTY_STRING);
            case OTHER:
                return Builder.indexFromValues(workspacePrefix,responseBody);
            case ERROR:
                return Builder.toJson(Builder.errorFromValues(workspacePrefix,responseBody));
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

    private static JsonNode string2JsonNode(String s) {
        try {
            return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
