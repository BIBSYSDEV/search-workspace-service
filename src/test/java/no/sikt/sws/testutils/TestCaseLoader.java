package no.sikt.sws.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class TestCaseLoader {
    private final Iterator<JsonNode> elements;

    public TestCaseLoader(String  resourceFileName) {
        URL resource = getClass().getClassLoader().getResource(resourceFileName);
        try {
            this.elements = JsonUtils.dtoObjectMapper.readTree(resource).elements();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Iterator<JsonNode> getElements() {
        return elements;
    }

    public ArrayList<TestCaseSws> getTestCases() {
        var retvalues = new ArrayList<TestCaseSws>();
        getElements().forEachRemaining(action -> retvalues.add(new TestCaseSws(action)));
        return retvalues;
    }

}
