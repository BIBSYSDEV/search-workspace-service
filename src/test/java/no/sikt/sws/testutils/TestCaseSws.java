package no.sikt.sws.testutils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

// WorkspaceIndexHandlerTestCase
public class TestCaseSws implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("indexName")
    private String indexName;

    @JsonProperty("requestGateway")
    private TestRequestSws requestGateway;

    @JsonProperty("requestOpensearch")
    private TestRequestSws requestOpensearch;

    @JsonProperty("response")
    private String response;

    @JsonProperty("responseStripped")
    private String responseStripped;

    public TestCaseSws(JsonNode testcase) {
        try {
            this.name = testcase.get("name").asText();
            this.indexName = testcase.get("indexName").asText();
            this.requestGateway = new TestRequestSws(testcase.get("requestGateway"));
            this.requestOpensearch = new TestRequestSws(testcase.get("requestOpensearch"));
            this.response = testcase.get("response").toPrettyString();
            this.responseStripped = testcase.get("responseStripped").toPrettyString();
        } catch (Exception e) {
            System.out.println("Problems parsing testcase: " + testcase.asText());
            throw e;
        }

    }

    public String getName() {
        return name;
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

    public TestRequestSws getRequestGateway() {
        return requestGateway;
    }

    public TestRequestSws getRequestOpensearch() {
        return requestOpensearch;
    }

    public String getIndexName() {
        return indexName;
    }
}
