package no.sikt.sws.testutils;

import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.models.opensearch.OpenSearchCommandKind;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.URI;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;

// WorkspaceIndexHandlerTestCase
public class TestCaseSws implements Serializable, Comparable<TestCaseSws> {
    @JsonProperty("name")
    private String name;

    @JsonProperty("enabled")
    private boolean enabled = true;

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

    public boolean isEnabled() {
        return enabled;
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

    public boolean isIndexResponse() {
        return EMPTY_STRING.equals(requestGateway.getUrl())
            && HttpMethodName.GET == requestGateway.getMethod()
            && isResponseTest();
    }

    public boolean isIndexRequest() {
        var cmdKind = OpenSearchCommandKind.fromString(requestGateway.getUrl());

        return OpenSearchCommandKind.INDEX == cmdKind
            && HttpMethodName.GET == requestGateway.getMethod()
            && isRequestTest();
    }

    public boolean isRequestBodyTest() {
        return requestGateway != null
            && requestOpensearch != null
            && requestGateway.getBody() != null
            && requestOpensearch.getBody() != null;
    }

    public boolean isParamRequestTest()  {
        return
            requestGateway != null
                && requestOpensearch != null
                && !URI.create(requestGateway.getUrl()).getQuery().isEmpty()
                && !URI.create(requestOpensearch.getUrl()).getQuery().isEmpty();
    }

    @Override
    public String toString() {
        return  "[" + this.name + "]";
    }



    public static TestCaseSws fromJson(JsonNode jsonNode) {
        try {
            return dtoObjectMapper.treeToValue(jsonNode, TestCaseSws.class);
        } catch (Exception e) {
            System.out.println("Could not parse json as TestCase: " + jsonNode.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(@NotNull TestCaseSws o) {
        return this.name.compareTo(o.name);
    }

}
