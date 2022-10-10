package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.WorkspaceStripper;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;

import java.util.Map;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;

public class InternalIndexDto {


    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;

    @JsonProperty("index_link")
    public String indexLink;

    public InternalIndexDto() {

    }

    public InternalIndexDto(JsonNode aliases, JsonNode mappings, JsonNode settings, String indexLink) {
        this.aliases = aliases;
        this.mappings = mappings;
        this.settings = settings;
        this.indexLink = indexLink;

    }

    public static InternalIndexDto fromOpenSearchIndex(Map.Entry<String, OpenSearchIndexDto> mapEntry,
                                                       String workspacePrefix) {
        var name = mapEntry.getKey().replaceAll(workspacePrefix + "-", "");
        var openSearchIndex = mapEntry.getValue();
        var link = API_GATEWAY_URL + "/" + name;

        return new InternalIndexDto(
            WorkspaceStripper.removePrefix(openSearchIndex.aliases, workspacePrefix),
            openSearchIndex.mappings,
            WorkspaceStripper.removePrefix(openSearchIndex.settings,workspacePrefix),
            link
        );
    }

}
