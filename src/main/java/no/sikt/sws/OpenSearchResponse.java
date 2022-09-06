package no.sikt.sws;

import com.amazonaws.http.HttpResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OpenSearchResponse {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchResponse.class);
    @JsonProperty("body")
    private final String body;

    @JsonProperty("status")
    private final int status;

    public OpenSearchResponse(int status ,String body) {
        this.status = status;
        this.body = body;
    }

    public OpenSearchResponse(HttpResponse httpResponse) throws IOException {
        logger.info("httpResponse "+httpResponse.toString());
        var bytes = httpResponse.getContent().readAllBytes();

        this.status = httpResponse.getStatusCode();
        this.body = new String(bytes);
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "OpenSearchResponse{" +
                "status='" + status + '\'' +
                ", body=" + body +
                '}';
    }
}
