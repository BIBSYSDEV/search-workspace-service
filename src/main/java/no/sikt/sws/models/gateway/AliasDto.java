package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class AliasDto implements Dto {

    @JsonProperty("actions")
    public JsonNode actions;

    public AliasDto() {
    }

    public static AliasDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, AliasDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String strippedResponse(String workspacePrefix) {
        actions =  string2JsonNode.apply(actions.toString()
            .replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
            .replaceAll("(\"alias\".*?\")(.+?\")","$1" + workspacePrefix + "-$2"));
        return toJson.apply(this);
    }

    @Override
    public String toString() {
        return "{\"AliasDto\":{"
            + "  \"actions\":" + actions
            + "}}";
    }
}
