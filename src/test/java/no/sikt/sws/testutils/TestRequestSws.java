package no.sikt.sws.testutils;


import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
        return  (url == null) ? "" : url;
    }

    public String getBody() {
        return body; //.textValue();
    }

    public List<JsonNode> getBulkBody() {
        return Arrays.stream(body.split("\n"))
            .map(s -> {
                try {
                    return JsonUtils.dtoObjectMapper.readValue(s, JsonNode.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
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
