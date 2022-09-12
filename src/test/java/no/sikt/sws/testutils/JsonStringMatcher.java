package no.sikt.sws.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import org.mockito.ArgumentMatcher;


public class JsonStringMatcher implements ArgumentMatcher<String> {
    private final String expected;

    public JsonStringMatcher(String expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(String actual) {
        if (expected == null) {
            return actual == null;
        }

        if (actual.equals(expected)) {
            return true;
        }

        try {
            var expectedJson = JsonUtils.dtoObjectMapper.readTree(expected);
            var actualJson = JsonUtils.dtoObjectMapper.readTree(actual);

            return expectedJson.equals(actualJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
