package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:RightCurly")
public class HitsDto implements Dto {
    @JsonProperty("total")
    public JsonNode shards;

    @JsonProperty("max_score")
    @JsonInclude
    public Number maxScore;

    @JsonProperty("hits")
    public Collection<DocDto> hits;

    public HitsDto() {
    }

    @Override
    public String strippedResponse(String workspacePrefix) {
        hits.forEach(doc -> doc.strippedResponse(workspacePrefix));
        return toJson.apply(this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HitsDto.class.getSimpleName() + "[", "]")
                .add("shards=" + shards)
                .add("maxScore=" + maxScore)
                .add("hits=" + hits.stream()
                        .map(DocDto::toString)
                        .collect(Collectors.joining()))
                .toString();
    }
}
