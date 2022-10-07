package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListResourceServersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceServerScopeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UpdateResourceServerRequest;

import java.util.List;

import static software.amazon.awssdk.services.cognitoidentityprovider.model.ExplicitAuthFlowsType.*;

public class CognitoHandler extends ApiGatewayHandler<Void, Void> {

    public CognitoHandler() {
        super(Void.class);
    }

    @Override
    protected Void processInput(Void input, RequestInfo requestInfo, Context context) {

        var cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.EU_WEST_1)
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();

        var listResourceServersRequest = ListResourceServersRequest
                .builder()
                .userPoolId("eu-west-1_jcd2kPfRu")
                .maxResults(10)
                .build();
        var resources = cognitoClient.listResourceServers(listResourceServersRequest);

        var servers = resources.resourceServers();
        if (servers.isEmpty()) {
            throw new IllegalStateException("Should have a resource server.");
        }

        var newScope = ResourceServerScopeType.builder()
                .scopeName("TestScope")
                .scopeDescription("Testing Scope that should be deleted")
                .build();

        var updateRequest = UpdateResourceServerRequest
                .builder()
                .identifier(servers.get(0).identifier())
                .scopes(newScope)
                .build();

        cognitoClient.updateResourceServer(updateRequest);


        var createUserPoolRequest = CreateUserPoolClientRequest.builder()
                .userPoolId("eu-west-1_jcd2kPfRu")
                .clientName("NewClientAttempt")
                .allowedOAuthScopes(Lists.newArrayList("workspace", "TestScope"))
                .explicitAuthFlows(
                        List.of(
                                ALLOW_ADMIN_USER_PASSWORD_AUTH,
                                ALLOW_CUSTOM_AUTH,
                                ALLOW_REFRESH_TOKEN_AUTH,
                                ALLOW_USER_SRP_AUTH)
                )
                .refreshTokenValidity(30)
                .accessTokenValidity(15)
                .idTokenValidity(15)
                .build();
        cognitoClient.createUserPoolClient(createUserPoolRequest);
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, Void output) {
        return null;
    }
}
