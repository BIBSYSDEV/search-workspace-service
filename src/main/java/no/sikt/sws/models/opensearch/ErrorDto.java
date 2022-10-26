package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

public class ErrorDto extends Dto {

    @JsonProperty("error")
    public JsonNode error;

    @JsonProperty("status")
    public Number status;

    public ErrorDto() {
        super();
    }

    public static ErrorDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, ErrorDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ErrorDto stripper(String workspacePrefix) {
        var regex = toRegEx.apply(workspacePrefix);
        error = string2JsonNode.apply(error.toString().replaceAll(regex,EMPTY_STRING));
        return this;
    }

}
