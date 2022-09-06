package no.sikt.sws.exception;

import com.amazonaws.AmazonClientException;
import com.amazonaws.http.HttpResponse;

public class OpenSearchException extends AmazonClientException {

    HttpResponse response;
    public HttpResponse getResponse() {
        return response;
    }

    public OpenSearchException(HttpResponse response ) {
        super(response.getStatusCode() + " " + response.getStatusText());
        this.response = response;
    }
}
