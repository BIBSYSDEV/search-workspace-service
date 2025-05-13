package no.sikt.sws.testutils;

import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.models.opensearch.OpenSearchCommandKind;

import java.io.Serializable;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;

// WorkspaceIndexHandlerTestCase
@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.SystemPrintln"})
public class CaseSws implements Serializable, Comparable<CaseSws> {
    @JsonProperty("name")
    private String name;

    @JsonProperty("enabled")
    private static final boolean ENABLED = true;

    @JsonProperty("requestGateway")
    private RequestSws requestGateway;

    @JsonProperty("requestOpensearch")
    private RequestSws requestOpensearch;

    @JsonProperty("response")
    private JsonNode response;

    @JsonProperty("responseStripped")
    private JsonNode responseStripped;

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return ENABLED;
    }

    public RequestSws getRequestGateway() {
        return requestGateway;
    }

    public RequestSws getRequestOpensearch() {
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
                && requestGateway.getUrl().contains("?")
                && requestOpensearch.getUrl().contains("?");
    }

    @Override
    public String toString() {
        return  "[" + this.name + "]";
    }



    public static CaseSws fromJson(JsonNode jsonNode) {
        try {
            return dtoObjectMapper.treeToValue(jsonNode, CaseSws.class);
        } catch (Exception e) {
            System.out.println("Could not parse json as TestCase: " + jsonNode.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(CaseSws o) {
        return this.name.compareTo(o.name);
    }

}
