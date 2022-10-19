package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collection;

public class ShardsDto implements Dto {

    @JsonProperty("total")
    public Number total;

    @JsonProperty("successful")
    public Number successful;

    @JsonProperty("skipped")
    public Number skipped;

    @JsonProperty("failed")
    public Number failed;

    @JsonProperty("failures")
    public Collection<String> failures;

    public ShardsDto() {
    }

    public static ShardsDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, ShardsDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String strippedResponse(String workspacePrefix) {
        var regex = toRegEx.apply(workspacePrefix);
        if (hits != null) {
            hits.hits.forEach(docDto ->
                docDto.indexName = docDto.indexName.replaceFirst(workspacePrefix + "-", ""));
        } else {
            logger.info(toString());
        }
        if (shards.failures != null) {
            shards.failures.forEach( failure ->
                failure = failure.replaceAll(workspacePrefix + "-", ""));
        }
        return toJson.apply(this);
    }

}
