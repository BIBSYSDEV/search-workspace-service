package no.sikt.sws;

import org.junit.jupiter.api.Test;

import static java.lang.Integer.valueOf;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.json.JSONObject;

public class IndexResponseTest {

    @Test
    void shouldReturnSameString() {
        String testObj = randomString();
        var result = new IndexResponse(testObj);
        assertThat(result.message, is(equalTo(testObj)));
    }
}
