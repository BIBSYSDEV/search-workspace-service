package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.StringJoiner;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class SearchDto implements Dto {

    @JsonProperty("took")
    @JsonInclude(NON_NULL)
    public Number took;

    @JsonProperty("timed_out")
    @JsonInclude(NON_NULL)
    public Boolean timedOut;

    @JsonProperty("_shards")
    @JsonInclude(NON_NULL)
    public JsonNode shards;

    @JsonProperty("hits")
    @JsonInclude(NON_NULL)
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
