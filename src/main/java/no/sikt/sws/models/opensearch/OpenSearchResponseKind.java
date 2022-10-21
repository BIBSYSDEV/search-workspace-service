package no.sikt.sws.models.opensearch;

import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import static com.amazonaws.http.HttpMethodName.GET;


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
        String responseBody) {

        if (checkforError(responseBody)) {
            return ERROR;
        }

        switch (commandKind) {
            case ALIAS:
                return (httpMethod == GET) ?  CONTENT :  ACK;
            case DOC:
                return CONTENT;
            case BULK:
            case SEARCH:
                return CONTENT_COLLECTION;
            case MAPPING:
                return (httpMethod == GET) ?  CONTENT_COLLECTION : CONTENT;
            case INDEX:
                return (httpMethod == GET) ?  CONTENT_COLLECTION : ACK;
            default:
                throw new IllegalStateException("Unexpected value: " + commandKind);
        }
    }

    private static boolean checkforError(String responseBody) {
        var node = string2JsonNode(responseBody);
        return node.has("error");
    }

    private static JsonNode string2JsonNode(String s) {
        try {
            return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
