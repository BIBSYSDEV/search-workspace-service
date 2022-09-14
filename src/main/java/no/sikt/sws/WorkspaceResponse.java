package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public WorkspaceResponse(String workspace, String indexList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        this.accountIdentifier = workspace;
        this.indexList = objectMapper.readTree(indexList);
    }

}

