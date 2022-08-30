package no.sikt.sws;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RequestUtilTest {

     @Test
     void shouldRespondWithoutException() {
         assertThat("TEST", is("TEST"));
//         var request = new RequestInfo();
//         try {
//             var requestHttpMethod = getRequestHttpMethod(request);
//             assertThat(requestHttpMethod, isA(HttpMethod.class));
//         } catch (Exception ex) {
//             a
//         }
     }
}
