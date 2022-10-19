package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nva.commons.core.attempt.Try.attempt;

public class IndexDto implements Dto {

    @JsonProperty("aliases")
    public JsonNode aliases;

    @JsonProperty("mappings")
    public JsonNode mappings;

    @JsonProperty("settings")
    public JsonNode settings;


    public IndexDto() {
    }

    public IndexDto(JsonNode aliases, JsonNode mappings, JsonNode settings) {
        this.aliases = aliases;
        this.mappings = mappings;
        this.settings = settings;
    }

    public static IndexDto fromResponse(String opensearchResponse) {
        try {
            return objectMapper.readValue(opensearchResponse, IndexDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String strippedResponse(String workspacePrefix) {

        var regex = toRegEx.apply(workspacePrefix);
        Map<String, OpenSearchIndexDto> sourceMap = objectMapper.readValue(responseBody, new TypeReference<>() {});

        var retMap = sourceMap
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                mapEntry -> mapEntry.getKey().replaceAll(workspacePrefix + "-", ""),
                mapEntry -> fromOpenSearchIndex(mapEntry, workspacePrefix),
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(retMap)).orElseThrow();


        return toJson.apply(this);
    }
    @Override
    public String toString() {
        return "OpenSearchIndexDto{"
            + "aliases=" + aliases.toString()
            + ", mappings=" + mappings.toString()
            + ", settings=" + settings.toString()
            + '}';
    }
}
