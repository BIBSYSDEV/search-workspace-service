package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.StringJoiner;

public class ErrorDto implements Dto {

    @JsonProperty("error")
    public JsonNode error;

    @JsonProperty("status")
    public Number status;

    public ErrorDto() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ErrorDto.class.getSimpleName() + "[", "]")
                .add("error=" + error)
                .add("status=" + status)
                .toString();
    }
}
