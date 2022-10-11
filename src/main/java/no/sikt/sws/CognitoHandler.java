package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import no.sikt.sws.models.internal.CreateUserClientDto;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;

import static no.sikt.sws.constants.ApplicationConstants.BACKEND_SCOPE_RESOURCE_SERVER_NAME;
import static no.sikt.sws.constants.ApplicationConstants.USER_POOL_NAME;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ExplicitAuthFlowsType.*;

public class CognitoHandler extends ApiGatewayHandler<CreateUserClientDto, Void> {

    CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.EU_WEST_1)
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();

    private static final Logger logger = LoggerFactory.getLogger(CognitoHandler.class);

    public CognitoHandler() {
        super(CreateUserClientDto.class);
    }

    @Override
    protected Void processInput(CreateUserClientDto input, RequestInfo requestInfo, Context context) {

        logger.info("Raw input: '" + input + "'");
        logger.info("name: '" + input.name + "'");

        String userPoolId = getUserPoolId();


        var listUserPoolClientRequest = ListUserPoolClientsRequest.builder()
                .userPoolId(userPoolId)
                .maxResults(10)
                .build();

        var anyClientRequest = cognitoClient
                .listUserPoolClients(listUserPoolClientRequest)
                .userPoolClients().stream().findAny();

        if (anyClientRequest.isEmpty()) {
            throw new IllegalStateException("No UserPools exist!");
        }

        var describeClientRequest = DescribeUserPoolClientRequest.builder()
                .userPoolId(userPoolId)
                .clientId(anyClientRequest.get().clientId())
                .build();

        var describeClientResponse = cognitoClient
                .describeUserPoolClient(describeClientRequest)
                .userPoolClient();

        logger.info("UserPoolClientScope: " + describeClientResponse.clientName()
                + "\nWith Scopes: " + describeClientResponse.allowedOAuthScopes());



        var newScopeName = "TestScope";
        var newAppClientName = "NewAppClientAttempt";

        var serverIdentifier = getResourceServer(userPoolId);

        createScope(userPoolId, serverIdentifier, newScopeName);
        createAppClient(userPoolId, newScopeName, newAppClientName);
        return null;
    }

    private void createAppClient(String userPoolId, String scopeName, String appClientName) {
        var createUserPoolRequest = CreateUserPoolClientRequest.builder()
                .userPoolId(userPoolId)
                .clientName(appClientName)
                .allowedOAuthScopes(Lists.newArrayList(scopeName, OAuthFlowType.CLIENT_CREDENTIALS.toString()))
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
    }

    private void createScope(String userPoolId, ResourceServerType server, String scopeName) {

        var scopes = new ArrayList<>(server.scopes());

        var newScope = ResourceServerScopeType.builder()
                .scopeName(scopeName)
                .scopeDescription("Testing Scope that should be deleted") //TODO: delete or change this line
                .build();

        scopes.add(newScope);

        logger.info("Scopes: " + scopes);

        var updateRequest = UpdateResourceServerRequest
                .builder()
                .userPoolId(userPoolId)
                .identifier(server.identifier())
                .name(BACKEND_SCOPE_RESOURCE_SERVER_NAME)
                .scopes(scopes)
                .build();

        cognitoClient.updateResourceServer(updateRequest);
    }

    private ResourceServerType getResourceServer(String userPoolId) {
        var listResourceServersRequest = ListResourceServersRequest
                .builder()
                .userPoolId(userPoolId)
                .maxResults(50)
                .build();
        var resources = cognitoClient.listResourceServers(listResourceServersRequest);

        var server = resources
                .resourceServers()
                .stream().filter(s -> s.name().equals(BACKEND_SCOPE_RESOURCE_SERVER_NAME))
                .findFirst();
        if (server.isEmpty()) {
            throw new IllegalStateException("Should have a resource server.");
        }
        return server.get();
    }

    private String getUserPoolId() {
        var userPool = cognitoClient
                .listUserPools(ListUserPoolsRequest.builder().build())
                .userPools()
                .stream().filter(up -> up.name().equals(USER_POOL_NAME))
                .findFirst();

        if (userPool.isEmpty()) {
            throw new IllegalStateException("No userpools were found.");
        }
        return userPool.get().id();
    }

    @Override
    protected Integer getSuccessStatusCode(CreateUserClientDto input, Void output) {
        return 200;
    }
}
