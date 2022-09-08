package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.StringJoiner;

public class AuthorizerResponse implements Serializable {

    @JsonProperty("workspace")
    public final String workspace;

    @JsonProperty("userid")
    public final String userid;


    public AuthorizerResponse(String workspace, String userid) {
        this.workspace = workspace;
        this.userid = userid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AuthorizerResponse.class.getSimpleName() + ": {", "}")
                .add("\"workspace\":\"" + workspace + "\"")
                .add("\"userid\":\"" + userid + "\"")
                .toString();
    }
}
