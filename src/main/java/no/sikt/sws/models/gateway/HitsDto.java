package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

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

    public static HitsDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, HitsDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String strippedResponse(String workspacePrefix) {
        var regex = toRegEx.apply(workspacePrefix);
        error = error.replaceAll(regex,EMPTY_STRING);
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
