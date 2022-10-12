package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import no.sikt.sws.models.internal.CognitoCredentialsDto;
import no.sikt.sws.models.internal.CreateUserClientDto;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static no.sikt.sws.constants.ApplicationConstants.*;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ExplicitAuthFlowsType.*;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.TimeUnitsType.DAYS;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.TimeUnitsType.MINUTES;

public class CognitoHandler extends ApiGatewayHandler<CreateUserClientDto, CognitoCredentialsDto> {

    CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.EU_WEST_1)
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();

    private static final Logger logger = LoggerFactory.getLogger(CognitoHandler.class);

    private static final String allowedNameRegex = "^[a-zA-Z0-9]*$";

    public CognitoHandler() {
        super(CreateUserClientDto.class);
    }

    @Override
    protected CognitoCredentialsDto processInput(CreateUserClientDto input, RequestInfo requestInfo, Context context) {

        if (input == null || input.name == null) {
            throw new IllegalStateException("Request does nt include name");
        }
        if (!input.name.matches(allowedNameRegex)) {
            throw new IllegalStateException("Name contains illegal chars. Should only be letters and numbers");
        }

        var userPoolId = getUserPoolId();

        var serverIdentifier = getResourceServer(userPoolId);


        var newScopeName = "workspace-" + input.name.toLowerCase(Locale.getDefault());
        var appClientName = "BackendApplication" + StringUtils.capitalize(input.name) + "Client";

        createScope(userPoolId, serverIdentifier, newScopeName);
        createAppClient(userPoolId, newScopeName, appClientName);

        return getClientCredentials(userPoolId, appClientName);
    }

    private CognitoCredentialsDto getClientCredentials(String userPoolId, String clientName) {
        var listClientRequest = ListUserPoolClientsRequest.builder()
                .maxResults(50)
                .userPoolId(userPoolId)
                .build();

        var listClientResponse = cognitoClient.listUserPoolClients(listClientRequest);
        var client = listClientResponse.userPoolClients().stream().filter(
                c -> clientName.equals(c.clientName())
        ).findFirst();

        if (client.isEmpty()) {
            throw new IllegalStateException("Should have a " + clientName);
        }

        var describeClientRequest = DescribeUserPoolClientRequest.builder()
                .userPoolId(userPoolId)
                .clientId(client.get().clientId())
                .build();
        var clientDescription = cognitoClient.describeUserPoolClient(describeClientRequest)
                .userPoolClient();

        return new CognitoCredentialsDto(clientDescription.clientId(), clientDescription.clientSecret());
    }

    private void createAppClient(String userPoolId, String name, String appClientName) {

        var tokenValidityUnitTypes = TokenValidityUnitsType.builder()
                .refreshToken(DAYS)
                .accessToken(MINUTES)
                .idToken(MINUTES)
                .build();

        var createUserPoolRequest = CreateUserPoolClientRequest.builder()
                .userPoolId(userPoolId)
                .clientName(appClientName)
                .generateSecret(true)
                .allowedOAuthScopes(Lists.newArrayList(
                        SCOPE_IDENTIFIER + "/workspace",
                        SCOPE_IDENTIFIER + "/" + name))
                .allowedOAuthFlowsWithStrings("client_credentials")
                .allowedOAuthFlowsUserPoolClient(true)
                .explicitAuthFlows(
                        List.of(
                                ALLOW_ADMIN_USER_PASSWORD_AUTH,
                                ALLOW_CUSTOM_AUTH,
                                ALLOW_REFRESH_TOKEN_AUTH,
                                ALLOW_USER_SRP_AUTH)
                )
                .tokenValidityUnits(tokenValidityUnitTypes)
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
                .scopeDescription("Scope for " + scopeName)
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
    protected Integer getSuccessStatusCode(CreateUserClientDto input, CognitoCredentialsDto output) {
        return 200;
    }
}
