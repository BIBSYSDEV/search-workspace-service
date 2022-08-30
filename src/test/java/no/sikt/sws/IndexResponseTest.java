package no.sikt.sws;

import org.junit.jupiter.api.Test;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class IndexResponseTest {

    @Test
    void shouldReturnSameString() {
        var randomString = randomString();
        var result = new IndexResponse(randomString);
        assertThat(result.message, is(equalTo(randomString)));
    }
}
