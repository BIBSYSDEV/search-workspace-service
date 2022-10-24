package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nva.commons.core.attempt.Try.attempt;

public class IndexesDto implements Dto {

    Map<String, IndexDto> sourceMap;

    @Override
    public String strippedResponse(String workspacePrefix) {

        var retMap = sourceMap
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                mapEntry -> mapEntry.getKey().replaceFirst(workspacePrefix + "-", ""),
                mapEntry -> stripp(mapEntry, workspacePrefix),
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(retMap)).orElseThrow();
    }

    private IndexDto stripp(Map.Entry<String, IndexDto> mapEntry, String workspacePrefix) {
        var index = mapEntry.getValue();
        index.strippedResponse(workspacePrefix);
        return index;
    }

    public static IndexesDto fromResponse(String opensearchResponse) {
        try {
            var dto = new IndexesDto();
            dto.sourceMap = objectMapper.readValue(opensearchResponse, new TypeReference<>() {});
            return dto;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
