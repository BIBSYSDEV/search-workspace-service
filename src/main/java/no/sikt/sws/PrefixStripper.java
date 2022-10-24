package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.constants.ApplicationConstants;
import no.sikt.sws.models.gateway.Builder;
import no.sikt.sws.models.opensearch.OpenSearchCommandKind;
import no.sikt.sws.models.opensearch.OpenSearchResponseKind;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;
import nva.commons.core.JacocoGenerated;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.sikt.sws.constants.ApplicationConstants.REQUIRED_PARAMETER_IS_NULL;

@JacocoGenerated
public class PrefixStripper {


    /**
     * Strips prefix from body.
     * @param node JsonNode to strip
     * @param workspacePrefix prefix to strip
     * @return stripped JsonNode
     */
    public static JsonNode node(JsonNode node, String workspacePrefix) {
        if (workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL + ApplicationConstants.WORKSPACE_PREFIX);
        }
        if (node == null) {
            return null;
        }

        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

        return string2JsonNode(
            node.toString()
                .replaceAll(regex, EMPTY_STRING));
    }

    /**
     * Strips prefix from body.
     *
     * @param commandKind     OpenSearchCommand
     * @param responseKind    OpenSearchResponseKind
     * @param workspacePrefix prefix to strip
     * @param responseBody    body to strip
     * @return stripped body
     */
    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    public static String body(OpenSearchCommandKind commandKind,
                              OpenSearchResponseKind responseKind,
                              String workspacePrefix,
                              String responseBody) throws BadRequestException {

        validateParameters(workspacePrefix, responseBody);

        //Regex that matches '{workspace}-' preceded by ' ', '/', '[' or '"'
        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

        switch (responseKind) {
            case ACK:
                return responseBody.replaceAll(regex, EMPTY_STRING);
            case ERROR:
                return Builder.errorFromValues(workspacePrefix,responseBody);
            case CONTENT:
                switch (commandKind) {
                    case ALIAS:
                    case MAPPING:
                        return responseBody.replaceAll(regex, EMPTY_STRING);
                    case DOC:
                        return Builder.docFromValues(workspacePrefix,responseBody);
                    default:
                        throw new IllegalStateException("Unexpected value: " + commandKind);
                }
            case CONTENT_COLLECTION:
                switch (commandKind) {
                    case BULK:
                    case MAPPING:
                        return responseBody.replaceAll(regex, EMPTY_STRING);
                    case INDEX:
                        return Builder.indexFromValues(workspacePrefix,responseBody);
                    case SEARCH:
                        return Builder.searchFromValues(workspacePrefix,responseBody);
                    default:
                        throw new IllegalStateException("Unexpected value: " + commandKind);
                }
            default:
                throw new IllegalStateException("Unexpected value: " + commandKind);
        }
    }

    private static void validateParameters(String workspacePrefix, String responseBody) {
        if (responseBody == null || workspacePrefix == null) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((responseBody == null) ? "[responseBody] " : EMPTY_STRING)
                    + ((workspacePrefix == null) ? ApplicationConstants.WORKSPACE_PREFIX : EMPTY_STRING));
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
