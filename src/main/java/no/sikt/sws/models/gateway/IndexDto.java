package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.PrefixStripper;

public class IndexDto implements Dto {

    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;

    public IndexDto() {
    }

    public static IndexDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, IndexDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String strippedResponse(String workspacePrefix) {

        aliases = PrefixStripper.node(aliases, workspacePrefix);
        settings = PrefixStripper.node(settings,workspacePrefix);

        return toJson.apply(this);
    }

    @Override
    public String toString() {
        return "OpenSearchIndexDto{"
            + "aliases=" + aliases.toString()
            + ", mappings=" + mappings.toString()
            + ", settings=" + settings.toString()
            + '}';
    }
}
