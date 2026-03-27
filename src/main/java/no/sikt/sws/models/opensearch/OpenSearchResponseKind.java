package no.sikt.sws.models.opensearch;

import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.BadRequestException;

import static com.amazonaws.http.HttpMethodName.GET;
import static nva.commons.core.attempt.Try.attempt;

@SuppressWarnings({"PMD.OnlyOneReturn", "PMD.CyclomaticComplexity"})
public enum OpenSearchResponseKind {
    ACK("acknowledged"),
    ERROR("error"),
    CONTENT("content"),
    CONTENT_COLLECTION("content-collection")
    ;

    private final String val;

    OpenSearchResponseKind(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    public static OpenSearchResponseKind fromString(
        HttpMethodName httpMethod,
        OpenSearchCommandKind commandKind,
        String responseBody) throws BadRequestException {

        if (isErrorResponse(responseBody)) {
            return ERROR;
        }

        switch (commandKind) {
            case ALIAS:
                return (httpMethod == GET) ?  CONTENT :  ACK;
            case DOC:
                return CONTENT;
            case BULK:
            case SEARCH:
            case SCROLL:
                return CONTENT_COLLECTION;
            case MAPPING:
            case INDEX:
                return (httpMethod == GET) ?  CONTENT_COLLECTION : ACK;
            default:
                throw new BadRequestException(commandKind.name());
        }
    }

    private static boolean isErrorResponse(String responseBody) {
        return attempt(() -> JsonUtils.dtoObjectMapper.readTree(responseBody))
                   .toOptional()
                   .filter(OpenSearchResponseKind::isError)
                   .isPresent();
    }

    private static boolean isError(JsonNode node) {
        return node.has("error") && node.has("status");
    }
}
