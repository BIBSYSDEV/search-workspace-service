package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

public class DocDto implements Dto {
    @JsonProperty("_index")
    public String indexName;

    @JsonProperty("_type")
    public String type;

    @JsonProperty("_id")
    public String id;

    @JsonProperty("_version")
    public Number version;

    @JsonProperty("result")
    public String result;

    @JsonProperty("_shards")
    public JsonNode shards;

    @JsonProperty("_seq_no")
    public Number seqNo;

    @JsonProperty("_primary_term")
    public Number primaryTerm;

    @JsonProperty("found")
    public Boolean found;

    @JsonProperty("_score")
    public Number score;

    @JsonProperty("_links")
    public Collection<String> links;

    @JsonProperty("_source")
    public JsonNode source;

    @JsonProperty("sort")
    public List<Integer> sort;


    public DocDto() {
    }

    @Override
    public String strippedResponse(String workspacePrefix) {
        indexName = indexName.replaceFirst(workspacePrefix + "-",EMPTY_STRING);
        return toJson.apply(this);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", DocDto.class.getSimpleName() + "[", "]")
                .add("indexName='" + indexName + "'")
                .add("id='" + id + "'")
                .toString();
    }

    public static DocDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, DocDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
