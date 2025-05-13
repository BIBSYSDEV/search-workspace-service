package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import nva.commons.core.JacocoGenerated;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

@SuppressWarnings({"PMD.OnlyOneReturn", "PMD.AvoidThrowingRawExceptionTypes", "PMD.BooleanGetMethodName"})
public class DocDto extends Dto {
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
    public List<Object> sort;

    @Override
    public DocDto stripper(String workspacePrefix) {
        indexName = indexName.replaceFirst(workspacePrefix + "-",EMPTY_STRING);
        return this;
    }

    public static DocDto fromResponse(String opensearchResponse) {
        try {
            return OBJECT_MAPPER.readValue(opensearchResponse, DocDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIndexName() {
        return indexName;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Number getVersion() {
        return version;
    }

    public String getResult() {
        return result;
    }

    public JsonNode getShards() {
        return shards;
    }

    public Number getSeqNo() {
        return seqNo;
    }

    public Number getPrimaryTerm() {
        return primaryTerm;
    }

    public Boolean getFound() {
        return found;
    }

    public Number getScore() {
        return score;
    }

    public Collection<String> getLinks() {
        return links;
    }

    public JsonNode getSource() {
        return source;
    }

    public List<Object> getSort() {
        return sort;
    }

    @Override
    @JacocoGenerated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocDto docDto = (DocDto) o;
        return Objects.equals(getIndexName(), docDto.getIndexName())
            && Objects.equals(getType(), docDto.getType())
            && Objects.equals(getId(), docDto.getId())
            && Objects.equals(getVersion(), docDto.getVersion())
            && Objects.equals(getResult(), docDto.getResult())
            && Objects.equals(getShards(), docDto.getShards())
            && Objects.equals(getSeqNo(), docDto.getSeqNo())
            && Objects.equals(getPrimaryTerm(), docDto.getPrimaryTerm())
            && Objects.equals(getFound(), docDto.getFound())
            && Objects.equals(getScore(), docDto.getScore())
            && Objects.equals(getLinks(), docDto.getLinks())
            && Objects.equals(getSource(), docDto.getSource())
            && Objects.equals(getSort(), docDto.getSort());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getIndexName(), getType(), getId(), getVersion(), getResult(), getShards(), getSeqNo(),
            getPrimaryTerm(), getFound(), getScore(), getLinks(), getSource(), getSort());
    }
}
