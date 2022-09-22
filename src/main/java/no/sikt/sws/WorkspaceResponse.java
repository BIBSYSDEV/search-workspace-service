package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;

public class WorkspaceResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("account_identifier")
    public String accountIdentifier;

    @JsonProperty("index_list")
    public JsonNode indexList;

    @JsonProperty("create_index_link")
    public String createIndexLink;

    public WorkspaceResponse() throws JsonProcessingException {
    }

    public WorkspaceResponse(
            String accountIdentifier,
            JsonNode indexList,
            String createIndexLink
    ) throws JsonProcessingException {
        this.accountIdentifier = accountIdentifier;
        this.indexList = indexList;
        this.createIndexLink = createIndexLink;
    }

    public static WorkspaceResponse fromValues(String workspace, String indexList) throws JsonProcessingException {

        JsonNode indexListJson = objectMapper.readTree(indexList);
        String createIndexLink = "";

        if (indexListJson.isEmpty()) {
            createIndexLink = API_GATEWAY_URL + "/index_name";
        }

        return new WorkspaceResponse(workspace, indexListJson, createIndexLink);
    }
}
