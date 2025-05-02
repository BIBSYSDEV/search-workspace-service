package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;

@SuppressWarnings("checkstyle:RightCurly")
public class HitsDto extends Dto {
    @JsonProperty("total")
    public JsonNode shards;

    @JsonProperty("max_score")
    @JsonInclude
    public Number maxScore;

    @JsonProperty("hits")
    public Collection<DocDto> hits;

    @Override
    public HitsDto stripper(String workspacePrefix) {
        hits.forEach(doc -> doc.stripper(workspacePrefix));
        return this;
    }

}
