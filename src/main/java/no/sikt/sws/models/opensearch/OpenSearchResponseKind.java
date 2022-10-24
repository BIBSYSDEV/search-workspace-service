package no.sikt.sws.models.opensearch;

import com.amazonaws.http.HttpMethodName;
import no.sikt.sws.models.gateway.ErrorDto;
import nva.commons.apigateway.exceptions.BadRequestException;

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
        String responseBody) throws BadRequestException {

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
            case INDEX:
                return (httpMethod == GET) ?  CONTENT_COLLECTION : ACK;
            default:
                throw new BadRequestException(commandKind.name());
        }
    }

    private static boolean checkforError(String responseBody) {
        var node = ErrorDto.string2JsonNode.apply(responseBody);
        return node.has("error") && node.has("status");
    }


}
