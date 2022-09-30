package no.sikt.sws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.models.internal.InternalIndexDto;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;

import java.util.Map;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;

public class WorkspaceResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("account_identifier")
    public String accountIdentifier;

    @JsonProperty("index_list")
    Map<String, InternalIndexDto> indexList;

    @JsonProperty("create_index_link")
    public String createIndexLink;
    
    public WorkspaceResponse(
            String accountIdentifier,
            Map<String, InternalIndexDto> indexList,
            String createIndexLink
    ) {
        this.accountIdentifier = accountIdentifier;
        this.indexList = indexList;
        this.createIndexLink = createIndexLink;
    }

    public static WorkspaceResponse fromValues(String workspacePrefix, String openSearchIndexList)
            throws JsonProcessingException {

        String createIndexLink = API_GATEWAY_URL + "/index_name";
        Map<String, OpenSearchIndexDto> openSearchIndexListMap =
                objectMapper.readValue(openSearchIndexList, new TypeReference<>() {});

        Map<String, InternalIndexDto> internalIndexListMap = openSearchIndexListMap
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, InternalIndexDto::fromOpenSearchIndex));


        return new WorkspaceResponse(workspacePrefix, internalIndexListMap, createIndexLink);
    }
}
