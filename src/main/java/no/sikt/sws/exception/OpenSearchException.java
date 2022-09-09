package no.sikt.sws.exception;

import com.amazonaws.AmazonClientException;

public class OpenSearchException extends AmazonClientException {

    private final String body;
    private final int status;

    public OpenSearchException(int status, String body) {
        super(status + " " + body);
        this.status = status;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

}
