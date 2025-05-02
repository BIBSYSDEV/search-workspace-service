package no.sikt.sws.testutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.LawOfDemeter"})
public class CaseLoader {
    private final List<JsonNode> elements;

    public CaseLoader(String resourceFileName) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource(resourceFileName);
        try {
            this.elements = JsonUtils.dtoObjectMapper.readValue(resource, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<JsonNode> getElements() {
        return elements;
    }

    public List<CaseSws> getTestCases() {
        return getElements()
            .stream()
            .map(CaseSws::fromJson)
            .sorted()
            .collect(Collectors.toList());
    }

    public CaseSws getTestCase(String name) {
        return getElements()
            .stream()
            .map(CaseSws::fromJson)
            .filter(e -> e.getName().equals(name))
            .findFirst()
            .orElseThrow();
    }

    public static final class Builder {

        private final Stream.Builder<CaseSws> streamBuilder;

        public Builder() {
            streamBuilder = Stream.builder();
        }

        public Builder loadResource(String filename) {
            new CaseLoader(filename)
                .getTestCases()
                .forEach(streamBuilder::add);
            return this;
        }

        public Stream<CaseSws> build() {
            return streamBuilder.build();
        }
    }
}
