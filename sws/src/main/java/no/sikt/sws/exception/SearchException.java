package no.sikt.sws.exception;

import nva.commons.apigateway.exceptions.ApiGatewayException;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class SearchException extends ApiGatewayException {

    public SearchException(String message, Exception exception) {
        super(exception, message);
    }

    @Override
    protected Integer statusCode() {
        return HTTP_INTERNAL_ERROR;
    }
}
