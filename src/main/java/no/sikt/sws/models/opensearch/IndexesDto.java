package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nva.commons.core.attempt.Try.attempt;

@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes"})
public class IndexesDto extends Dto {

    private Map<String, IndexDto> sourceMap;

    @Override
    public IndexesDto stripper(String workspacePrefix) {

        var retMap = sourceMap
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                mapEntry -> mapEntry.getKey().replaceFirst(workspacePrefix + "-", ""),
                mapEntry -> mapEntry.getValue().stripper(workspacePrefix),
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        sourceMap = retMap;
        return this;
    }

    public static IndexesDto fromResponse(String opensearchResponse) {
        try {
            var dto = new IndexesDto();
            dto.sourceMap = OBJECT_MAPPER.readValue(opensearchResponse, new TypeReference<>() {});
            return dto;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    @Override
    public  String toJsonCompact() {
        return attempt(() -> OBJECT_MAPPER
            .writer(getPrettyPrinterCompact())
            .writeValueAsString(sourceMap)).orElseThrow();
    }

}
