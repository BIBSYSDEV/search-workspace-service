package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.PrefixStripper;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static nva.commons.core.attempt.Try.attempt;

public class Builder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Dto docFromValues(String workspacePrefix, String opensearchResponse) {
        try {
            var instance = objectMapper.readValue(opensearchResponse, DocDto.class);
            instance.indexName = instance.indexName.replaceFirst(workspacePrefix + "-", "");
            return instance;
        } catch (JsonProcessingException ex) {
            return errorFromValues(workspacePrefix,opensearchResponse);
        }
    }

    public static Dto searchFromValues(String workspacePrefix, String opensearchResponse) {
        try {
            var instance = objectMapper.readValue(opensearchResponse, SearchDto.class);
            instance.hits.hits.forEach(docDto ->
                    docDto.indexName = docDto.indexName.replaceFirst(workspacePrefix + "-", ""));
            return instance;
        } catch (JsonProcessingException ex) {
            return errorFromValues(workspacePrefix,opensearchResponse);
        }
    }

    public static String indexFromValues(String workspacePrefix, String responseBody) {
        var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";
        try {
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

        } catch (JsonProcessingException e) {
            return attempt(() -> objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(errorFromValues(workspacePrefix, responseBody)))
                .or(() -> responseBody.replaceAll(regex, EMPTY_STRING)).get();
        }
    }

    private static Dto fromOpenSearchIndex(Map.Entry<String, OpenSearchIndexDto> mapEntry, String workspacePrefix) {
        //var name = mapEntry.getKey().replaceAll(workspacePrefix + "-", "");
        var openSearchIndex = mapEntry.getValue();

        return new IndexDto(
            PrefixStripper.node(openSearchIndex.aliases, workspacePrefix),
            openSearchIndex.mappings,
            PrefixStripper.node(openSearchIndex.settings, workspacePrefix)
        );
    }

    public static Dto errorFromValues(String workspacePrefix, String opensearchResponse) {
        try {
            var regex = "(?<=[ /\"\\[])" + workspacePrefix + "-";

            return objectMapper.readValue(
                    opensearchResponse.replaceAll(regex,EMPTY_STRING), ErrorDto.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String toJson(Dto dto)  {
        return attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(dto)).orElseThrow();
    }

}
