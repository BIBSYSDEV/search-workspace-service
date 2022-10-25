package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.StringJoiner;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

public class ErrorDto implements Dto {

    @JsonProperty("error")
    public String error;

    @JsonProperty("status")
    public Number status;

    public ErrorDto() {
    }

    public static ErrorDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, ErrorDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String strippedResponse(String workspacePrefix) {
        var regex = toRegEx.apply(workspacePrefix);
        error = error.replaceAll(regex,EMPTY_STRING);
        return toJson.apply(this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ErrorDto.class.getSimpleName() + "[", "]")
                .add("error=" + error)
                .add("status=" + status)
                .toString();
    }
}
