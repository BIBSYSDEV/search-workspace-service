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
               + "aliases=" +  ((aliases != null) ? aliases : null)
               + ", mappings=" +  ((mappings != null) ? mappings : null)
               + ", settings=" + ((aliases != null) ? aliases : null)
               + '}';
    }
}
