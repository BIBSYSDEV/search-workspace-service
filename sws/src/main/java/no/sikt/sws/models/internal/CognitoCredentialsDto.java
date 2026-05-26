package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CognitoCredentialsDto {

    @JsonProperty("client_id")
    public String clientId;

    @JsonProperty("client_secret")
    public String clientSecret;

    public CognitoCredentialsDto(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

}
