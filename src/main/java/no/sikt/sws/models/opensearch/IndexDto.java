package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.PrefixStripper;

public class IndexDto extends Dto {

    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;

    public IndexDto() {
        super();
    }

    public static IndexDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, IndexDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexDto stripper(String workspacePrefix) {

        aliases = PrefixStripper.node(aliases, workspacePrefix);
        settings = PrefixStripper.node(settings,workspacePrefix);

        return this;
    }

}
