package no.sikt.sws.models;

import nva.commons.apigateway.exceptions.ApiGatewayException;
import software.amazon.awssdk.http.HttpStatusCode;

public class RequestTooLargeException extends ApiGatewayException {

    public RequestTooLargeException(String message) {
        super(message);
    }

    @Override
    protected Integer statusCode() {
        return HttpStatusCode.REQUEST_TOO_LONG;
    }
}
