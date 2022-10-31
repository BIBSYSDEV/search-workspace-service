package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class AliasDto extends Dto {

    @JsonProperty("actions")
    public JsonNode actions;

    public static AliasDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, AliasDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AliasDto stripper(String workspacePrefix) {
        actions =  string2JsonNode.apply(actions.toString()
            .replaceAll("(\"index\".*?\")(.+?\")","$1" + workspacePrefix + "-$2")
            .replaceAll("(\"alias\".*?\")(.+?\")","$1" + workspacePrefix + "-$2"));
        return this;
    }

}
