package no.sikt.sws;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.StringJoiner;

public class Request implements Serializable {
    @JsonProperty("method")
    private String method;

    @JsonProperty("url")
    private String url;

    @JsonProperty("body")
    private String body;


    public Request() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]")
                .add("method='" + method + "'")
                .add("url=" + url)
                .add("body={...}")
                .toString();
    }
}
