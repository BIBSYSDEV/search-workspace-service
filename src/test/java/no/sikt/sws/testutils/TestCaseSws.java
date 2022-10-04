package no.sikt.sws.testutils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;

// WorkspaceIndexHandlerTestCase
public class TestCaseSws implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("requestGateway")
    private TestRequestSws requestGateway;

    @JsonProperty("requestOpensearch")
    private TestRequestSws requestOpensearch;

    @JsonProperty("response")
    private JsonNode response;

    @JsonProperty("responseStripped")
    private JsonNode responseStripped;

    public String getName() {
        return name;
    }

    public TestRequestSws getRequestGateway() {
        return requestGateway;
    }

    public TestRequestSws getRequestOpensearch() {
        return requestOpensearch;
    }

    public String getResponse() {
        return response != null ? response.toPrettyString() : null;
    }

    public String getResponseStripped() {
        return responseStripped != null ? responseStripped.toPrettyString() : null;
    }

    public boolean isResponseTest() {
        return response != null && responseStripped != null;
    }

    public boolean isRequestTest() {
        return requestGateway != null && requestOpensearch != null;
    }

    public boolean isRequestBodyTest() {
        return requestGateway != null && requestGateway.getBody() != null
                && requestOpensearch != null && requestOpensearch.getBody() != null;
    }

    @Override
    public String toString() {
        return this.name;
    }


    public static TestCaseSws fromJson(JsonNode jsonNode) {
        try {
            return dtoObjectMapper.treeToValue(jsonNode, TestCaseSws.class);
        } catch (Exception e) {
            System.out.println("Could not parse json as TestCase: " + jsonNode.toString());
            throw new RuntimeException(e);
        }
    }
}
