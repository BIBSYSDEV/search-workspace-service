package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.StringJoiner;

public class TestQuery implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("request")
    private Request request;

    @JsonProperty("response")
    private String response;

    @JsonProperty("responseStripped")
    private String responseStripped;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseStripped() {
        return responseStripped;
    }

    public void setResponseStripped(String responseStripped) {
        this.responseStripped = responseStripped;
    }

    public TestQuery() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TestQuery.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("request=" + request)
                .add("response=" + response.toString())
                .add("responseStripped=" + responseStripped)
                .toString();
    }
}
