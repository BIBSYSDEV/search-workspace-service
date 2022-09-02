package no.sikt.sws;

import com.amazonaws.Response;
import com.amazonaws.http.HttpMethodName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RestClientOpenSearchTest {


    @Test
    @Disabled("Disabled until we have mock for opensearch")
    void sendsRequestWithCorrectParams() throws IOException {
        RestClientOpenSearch restClientOpenSearch = new RestClientOpenSearch();

        Response response = restClientOpenSearch.sendRequest(HttpMethodName.PUT, "fredrik-test");

        assert (response.getHttpResponse().getStatusCode() == 200);
    }

}
