package no.sikt.sws.models.gateway;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.commons.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static nva.commons.core.attempt.Try.attempt;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Dto {

    @JsonIgnore
    Logger logger = LoggerFactory.getLogger(Dto.class);

    @JsonIgnore
    ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @JsonIgnore
    Function<String, String> toRegEx = prefix -> "(?<=[ /\"\\[])" + prefix + "-";

    @JsonIgnore
    Function<Dto,String> toJson = dto -> attempt(() -> objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(dto)).orElseThrow();

    @JsonIgnore
    Function<String, JsonNode> string2JsonNode = nodeAsString ->  attempt(() ->
        JsonUtils.dtoObjectMapper.readValue(nodeAsString, JsonNode.class)).get();

    @JsonIgnore
    String strippedResponse(String workspacePrefix);

}
