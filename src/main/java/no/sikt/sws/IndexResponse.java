package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexResponse {
    @JsonProperty("message")
    public final String message;

    public IndexResponse(String message) {
        this.message = message;
    }
}