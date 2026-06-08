package no.sikt.sws.exception;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

import nva.commons.apigateway.exceptions.ApiGatewayException;

public class SearchException extends ApiGatewayException {

  public SearchException(String message, Exception exception) {
    super(exception, message);
  }

  @Override
  protected Integer statusCode() {
    return HTTP_INTERNAL_ERROR;
  }
}
