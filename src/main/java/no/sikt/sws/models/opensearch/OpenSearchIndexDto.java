package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class OpenSearchIndexDto {

    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;

    @Override
    public String toString() {
        return "OpenSearchIndexDto{"
               + "aliases=" + aliases.toString()
               + ", mappings=" + mappings.toString()
               + ", settings=" + settings.toString()
               + '}';
    }
}
