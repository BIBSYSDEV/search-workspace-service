package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.PrefixStripper;
import no.sikt.sws.models.opensearch.OpenSearchIndexDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static nva.commons.core.attempt.Try.attempt;

public class Builder {

    private static final Logger logger = LoggerFactory.getLogger(Builder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DefaultPrettyPrinter getPrettyPrinter() {
        DefaultPrettyPrinter p = new DefaultPrettyPrinter();
        DefaultPrettyPrinter.Indenter i = new DefaultIndenter("  ", "\n");
        p.indentObjectsWith(i);
        return p;
    }
    private static final Function<String, String> toRegEx = prefix -> "(?<=[ /\"\\[])" + prefix + "-";

    public static String docFromValues(String workspacePrefix, String opensearchResponse) {
        var regex = toRegEx.apply(workspacePrefix);
        try {
            var instance = objectMapper.readValue(opensearchResponse, DocDto.class);
            instance.indexName = instance.indexName.replaceFirst(workspacePrefix + "-", "");
            return toJson(instance);
        } catch (JsonProcessingException ex) {
            logger.warn(ex.getMessage());
            return opensearchResponse.replaceAll(regex, EMPTY_STRING);
        }
    }

    public static String searchFromValues(String workspacePrefix, String opensearchResponse) {
        var regex = toRegEx.apply(workspacePrefix);
        try {
            var instance = objectMapper.readValue(opensearchResponse, SearchDto.class);
            instance.hits.hits.forEach(docDto ->
                    docDto.indexName = docDto.indexName.replaceFirst(workspacePrefix + "-", ""));
            return toJson(instance);
        } catch (JsonProcessingException ex) {
            logger.warn(ex.getMessage());
            return opensearchResponse.replaceAll(regex, EMPTY_STRING);
        }
    }

    public static String indexFromValues(String workspacePrefix, String responseBody) {
        var regex = toRegEx.apply(workspacePrefix);
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
//                .writer(getPrettyPrinter())
                .writeValueAsString(retMap)).orElseThrow();

        } catch (JsonProcessingException ex) {
            logger.warn(ex.getMessage());
            return responseBody.replaceAll(regex, EMPTY_STRING);
            //return attempt(() -> errorFromValues(workspacePrefix, responseBody))
            //    .or(() -> responseBody.replaceAll(regex, EMPTY_STRING)).get();
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

    public static String errorFromValues(String workspacePrefix, String opensearchResponse) {
        try {
            var regex = toRegEx.apply(workspacePrefix);
            var dto = objectMapper.readValue(
                    opensearchResponse.replaceAll(regex,EMPTY_STRING), ErrorDto.class);

            return toJson(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String toJson(Dto dto)  {
        return attempt(() -> objectMapper
            .writerWithDefaultPrettyPrinter()
//            .writer(getPrettyPrinter())
            .writeValueAsString(dto)).orElseThrow();
    }

}
