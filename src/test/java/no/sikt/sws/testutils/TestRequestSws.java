package no.sikt.sws.testutils;


import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.WorkspaceStripperTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.StringJoiner;

public class TestRequestSws implements Serializable {

    private final String method;

    private final String url;

    private final JsonNode body;

    private static final Logger logger = LoggerFactory.getLogger(TestRequestSws.class);

    public TestRequestSws(JsonNode request) {
        logger.info(request.toString());
        this.method = request.get("method").textValue();
        this.url = request.get("url").textValue();
        this.body = request.get("body");
    }

    public HttpMethodName getMethod() {
        return HttpMethodName.valueOf(method);
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body.textValue();
    }

    public JsonNode getBodyNode() {
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
