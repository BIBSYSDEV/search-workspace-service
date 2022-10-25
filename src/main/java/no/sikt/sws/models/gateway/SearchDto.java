package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.StringJoiner;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

public class SearchDto implements Dto {

    @JsonProperty("took")
    public Number took;

    @JsonProperty("timed_out")
    public Boolean timedOut;

    @JsonProperty("_shards")
    public ShardsDto shards;

    @JsonProperty("hits")
    public HitsDto hits;

    public SearchDto() {
    }

    public static SearchDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, SearchDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unused")
    public String strippedResponse(String workspacePrefix) {
        if (hits != null) {
            hits.hits.forEach(docDto ->
                docDto.indexName = docDto.indexName.replaceFirst(workspacePrefix + "-", EMPTY_STRING));
        } else {
            logger.info(toString());
        }
        if (shards != null && shards.failures != null) {
            shards.failures.forEach(failure ->
                failure = failure.replaceAll(workspacePrefix + "-", EMPTY_STRING));
        }
        return toJson.apply(this);
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
