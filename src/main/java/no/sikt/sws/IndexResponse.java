package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class IndexResponse {

    @JsonProperty("message")
    public JSONObject message;

    public IndexResponse(JSONObject message) {
        this.message = message;
    }
}
