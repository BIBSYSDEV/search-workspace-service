package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

public class SearchDto extends Dto {

    @JsonProperty("took")
    public Number took;

    @JsonProperty("timed_out")
    public Boolean timedOut;

    @JsonProperty("_shards")
    public ShardsDto shards;

    @JsonProperty("hits")
    public HitsDto hits;

    @JsonProperty("aggregations")
    public JsonNode aggregations;

    @JsonProperty("_scroll_id")
    public String scrollId;


    public SearchDto() {
        super();
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
    public SearchDto stripper(String workspacePrefix) {
        if (hits != null) {
            hits.hits.forEach(docDto ->
                docDto.indexName = docDto.indexName.replaceFirst(workspacePrefix + "-", EMPTY_STRING));
        } else {
            logger.info(toString());
        }
        if (shards != null) {
            shards.stripper(workspacePrefix);
        }

        return this;
    }

}
