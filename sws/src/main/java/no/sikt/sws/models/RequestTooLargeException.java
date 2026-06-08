package no.sikt.sws.models;

import static java.net.HttpURLConnection.HTTP_ENTITY_TOO_LARGE;

import nva.commons.apigateway.exceptions.ApiGatewayException;

public class RequestTooLargeException extends ApiGatewayException {

  public RequestTooLargeException(String message) {
    super(message);
  }

  @Override
  protected Integer statusCode() {
    return HTTP_ENTITY_TOO_LARGE;
  }
}
