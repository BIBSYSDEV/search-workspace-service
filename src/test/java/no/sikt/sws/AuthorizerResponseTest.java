package no.sikt.sws;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class AuthorizerResponseTest {

    @Test
    void shouldReturnSameString() {
        var randomstring = randomString();
        var ar = new AuthorizerResponse("AR-workspace", randomstring);
        System.out.println(ar.toString());
        assertThat(ar.userid, is(equalTo(randomstring)));

    }
}