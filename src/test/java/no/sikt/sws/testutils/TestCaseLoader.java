package no.sikt.sws.testutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class TestCaseLoader {
    private final List<JsonNode> elements;

    public TestCaseLoader(String  resourceFileName) {
        URL resource = getClass().getClassLoader().getResource(resourceFileName);
        try {
            this.elements = JsonUtils.dtoObjectMapper.readValue(resource, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<JsonNode> getElements() {
        return elements;
    }

    public List<TestCaseSws> getTestCases() {
        return getElements()
                .stream()
                .map(TestCaseSws::fromJson)
                .sorted()
                .collect(Collectors.toList());
    }

    public TestCaseSws getTestCase(String name) {
        return getElements()
                .stream()
                .map(TestCaseSws::fromJson)
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

}
