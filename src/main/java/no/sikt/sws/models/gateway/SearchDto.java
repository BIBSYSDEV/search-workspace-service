package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.StringJoiner;

public class SearchDto implements Dto {

    @JsonProperty("took")
    public Number took;

    @JsonProperty("timed_out")
    public Boolean timedOut;

    @JsonProperty("_shards")
    public JsonNode shards;

    @JsonProperty("hits")
    public HitsDto hits;

    public SearchDto() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchDto.class.getSimpleName() + "[", "]")
                .add("took=" + took)
                .add("timedOut=" + timedOut)
                .add("shards=" + shards)
                .add("hits=" + hits)
                .toString();
    }
}
