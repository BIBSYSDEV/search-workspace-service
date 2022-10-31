package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.commons.json.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static nva.commons.core.attempt.Try.attempt;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Dto {

    @JsonIgnore
    Logger logger = LoggerFactory.getLogger(getClass().getName());

    @JsonIgnore
    static final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @JsonIgnore
    Function<String, String> toRegEx = prefix -> "(?<=[ /\"\\[])" + prefix + "-";

    @JsonIgnore
    Function<String, JsonNode> string2JsonNode = nodeAsString -> attempt(() ->
        JsonUtils.dtoObjectMapper.readValue(nodeAsString, JsonNode.class)).get();

    @JsonIgnore
    @NotNull
    protected static DefaultPrettyPrinter getPrettyPrinterFormatted() {
        DefaultPrettyPrinter p = new DefaultPrettyPrinter();
        DefaultPrettyPrinter.Indenter i = new DefaultIndenter("  ", "\n");
        p.indentArraysWith(i);
        p.indentObjectsWith(i);
        return p;
    }

    @JsonIgnore
    @NotNull
    public static DefaultPrettyPrinter getPrettyPrinterCompact() {
        DefaultPrettyPrinter p = new DefaultPrettyPrinter();
        DefaultPrettyPrinter.Indenter i = new DefaultIndenter(EMPTY_STRING, EMPTY_STRING);
        p.indentArraysWith(i);
        p.indentObjectsWith(i);
        return p;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return  attempt(() -> objectMapper
            .writer(getPrettyPrinterFormatted())
            .writeValueAsString(this)).orElseThrow();
    }

    @JsonIgnore
    public  String toJsonCompact() {
        return attempt(() -> objectMapper
            .writer(getPrettyPrinterCompact())
            .writeValueAsString(this)).orElseThrow();
    }

    @JsonIgnore
    abstract Dto stripper(String workspacePrefix);

}

