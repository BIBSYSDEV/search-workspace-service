package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.StringJoiner;

public class DocDto implements Dto {
    @JsonProperty("_index")
    public String indexName;

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

    public DocDto() {
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", DocDto.class.getSimpleName() + "[", "]")
                .add("indexName='" + indexName + "'")
                .add("id='" + id + "'")
                .toString();
    }

}
