package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.constants.ApplicationConstants;
import no.sikt.sws.models.opensearch.*;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;
import nva.commons.core.JacocoGenerated;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.sikt.sws.constants.ApplicationConstants.REQUIRED_PARAMETER_IS_NULL;

@JacocoGenerated
public class PrefixStripper {

    public static final String UNEXPECTED_VALUE = "Unexpected value: ";

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

        var regex = "([ /\"\\[])(" + workspacePrefix + "-)(.*?[\"/ ])";

        return string2JsonNode(
            node.toString()
                .replaceAll(regex, "$1$3"));
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

        validateParameters(workspacePrefix, responseBody,commandKind);

        //Regex that matches '{workspace}-' preceded by ' ', '/', '[' or '"'
        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

        switch (responseKind) {
            case ACK:
                return responseBody.replaceAll(regex, EMPTY_STRING);
            case ERROR:
                return ErrorDto.fromResponse(responseBody).stripper(workspacePrefix)
                    .toJsonCompact();
            case CONTENT:
                switch (commandKind) {
                    case ALIAS:
                    case MAPPING:
                        return IndexDto.fromResponse(responseBody).stripper(workspacePrefix)
                            .toJsonCompact();
                    case DOC:
                        return DocDto.fromResponse(responseBody).stripper(workspacePrefix)
                            .toJsonCompact();
                    default:
                        throw new IllegalStateException(UNEXPECTED_VALUE + commandKind.name());
                }
            case CONTENT_COLLECTION:
                switch (commandKind) {
                    case BULK:
                    case MAPPING:
                        return responseBody.replaceAll(regex, EMPTY_STRING);    // stripping index names in body.
                    case INDEX:
                        return IndexesDto.fromResponse(responseBody).stripper(workspacePrefix)
                            .toJsonCompact();
                    case SEARCH:
                        return SearchDto.fromResponse(responseBody).stripper(workspacePrefix)
                            .toJsonCompact();
                    default:
                        throw new IllegalStateException(UNEXPECTED_VALUE + commandKind.name());
                }
            default:
                throw new IllegalStateException(UNEXPECTED_VALUE + commandKind.name());
        }
    }

    private static void validateParameters(String workspacePrefix, String responseBody,
                                           OpenSearchCommandKind commandKind) {
        if (responseBody == null || workspacePrefix == null || commandKind.isNotValid()) {
            throw new IllegalArgumentException(REQUIRED_PARAMETER_IS_NULL
                    + ((responseBody == null) ? "[responseBody] " : EMPTY_STRING)
                + ((workspacePrefix == null) ? ApplicationConstants.WORKSPACE_PREFIX : EMPTY_STRING)
                + (commandKind.isNotValid() ? "[CommandKind]" : EMPTY_STRING));
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
