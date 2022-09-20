package no.sikt.sws.testutils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

// WorkspaceIndexHandlerTestCase
public class TestCaseSws implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("request")
    private TestRequestSws request;

    @JsonProperty("response")
    private String response;

    @JsonProperty("responseStripped")
    private String responseStripped;

    public TestCaseSws(JsonNode testcase) {
        this.name =  testcase.get("name").asText();
        this.request = new TestRequestSws(testcase.get("request"));
        this.response = testcase.get("response").toPrettyString();
        this.responseStripped = testcase.get("responseStripped").toPrettyString();
    }

    public String getName() {
        return name;
    }

    public TestRequestSws getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public String getResponseStripped() {
        return responseStripped;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
