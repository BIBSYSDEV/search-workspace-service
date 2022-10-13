package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class IndexDto implements Dto {

    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;

    public IndexDto(JsonNode aliases, JsonNode mappings, JsonNode settings) {
        this.aliases = aliases;
        this.mappings = mappings;
        this.settings = settings;
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
