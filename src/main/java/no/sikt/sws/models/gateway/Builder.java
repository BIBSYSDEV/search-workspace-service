package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static no.sikt.sws.WorkspaceStripper.EMPTY_STRING;
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


    public static String toJson(Object dto)  {
        return attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(dto)).orElseThrow();
    }

}
