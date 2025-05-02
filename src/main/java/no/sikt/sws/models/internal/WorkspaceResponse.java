package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;

public class WorkspaceResponse {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @JsonProperty("account_identifier")
    public String accountIdentifier;

    @JsonProperty("index_list")
    public Map<String, InternalIndexDto> indexList;

    @JsonProperty("create_index_link")
    public String createIndexLink;

    @SuppressWarnings("unused")
    public WorkspaceResponse() {
    }

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
                OBJECT_MAPPER.readValue(openSearchIndexList, new TypeReference<>() {});

        var internalIndexListMap = openSearchIndexListMap
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                    mapEntry -> mapEntry.getKey().replaceAll(workspacePrefix + "-", ""),
                    mapEntry -> InternalIndexDto.fromOpenSearchIndex(mapEntry, workspacePrefix),
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return new WorkspaceResponse(workspacePrefix, internalIndexListMap, createIndexLink);
    }
}
