package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class WorkspaceResponse {

    @JsonProperty("account_identifier")
    public String accountIdentifier;

    @JsonProperty("index_list")
    public JsonNode indexList;

    public WorkspaceResponse() {
    }

    public WorkspaceResponse(String accountIdentifier, JsonNode indexList) {
        this.accountIdentifier = accountIdentifier;
        this.indexList = indexList;
    }
}

