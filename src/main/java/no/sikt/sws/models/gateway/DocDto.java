package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.StringJoiner;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class DocDto implements Dto {
    @JsonProperty("_index")
    @JsonInclude(NON_NULL)
    public String indexName;

    @JsonProperty("_id")
    @JsonInclude(NON_NULL)
    public String id;


    @JsonProperty("_version")
    @JsonInclude(NON_NULL)
    public Number version;

    @JsonProperty("_seq_no")
    @JsonInclude(NON_NULL)
    public Number seqNo;

    @JsonProperty("_primary_term")
    @JsonInclude(NON_NULL)
    public Number primaryTerm;

    @JsonProperty("found")
    @JsonInclude(NON_NULL)
    public Boolean found;

    @JsonProperty("_score")
    @JsonInclude(NON_NULL)
    public Number score;

    @JsonProperty("_source")
    @JsonInclude(NON_NULL)
    public JsonNode source;

    public DocDto() {
    }


    public DocDto(String indexName, String id, Number version, Number seqNo, Number primaryTerm, Boolean found, JsonNode source) {
        this.indexName = indexName;
        this.id = id;
        this.version = version;
        this.seqNo = seqNo;
        this.primaryTerm = primaryTerm;
        this.found = found;
        this.source = source;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DocDto.class.getSimpleName() + "[", "]")
                .add("indexName='" + indexName + "'")
                .add("id='" + id + "'")
                .toString();
    }

}
