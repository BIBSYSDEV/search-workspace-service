package no.sikt.sws.testutils;


import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.WorkspaceStripperTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.StringJoiner;

public class TestRequestSws implements Serializable {

    @JsonProperty("method")
    private String method;

    @JsonProperty("url")
    private String url;

    @JsonProperty("body")
    private String body;

    public HttpMethodName getMethod() {
        return HttpMethodName.valueOf(method);
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n\t",  "{\n\t", "\n}")
                .add("method: " + method)
                .add("url: " + url)
                .add("body: " + body)
                .toString();
    }
}
