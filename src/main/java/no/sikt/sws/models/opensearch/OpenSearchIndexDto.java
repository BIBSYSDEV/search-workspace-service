package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class OpenSearchIndexListDto {

    @JsonProperty("aliases")
    private JsonNode aliases;

    @JsonProperty("mappings")
    private JsonNode mappings;

    @JsonProperty("settings")
    private JsonNode settings;
}
