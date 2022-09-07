package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.builders.OpenSearchRequestBuilder;
import nva.commons.apigateway.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class WorkspaceAuthorizer {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceAuthorizer.class);
    private static final String WORKSPACE ="workspace-authorizer-authors";  // => opensearch index
    private static final String QUERY ="/_search?q=workspace:%s&author:%s" ;

    private final OpenSearchClient openSearchClient = new OpenSearchClient();


    public boolean authorized(RequestInfo requestInfo, Context context) {
        logger.debug(context.toString());

        var httpMethod = RequestUtil.getRequestHttpMethod(requestInfo);
        var workspace = RequestUtil.getWorkspace(requestInfo);
        var headers = requestInfo.getHeaders();
        var userId = context.getIdentity().getIdentityId();

        var path = String.format(WORKSPACE+QUERY,workspace,userId);

        var valid = false;

        switch (httpMethod) {
            case POST, PUT, DELETE, PATCH -> {
                var request = new OpenSearchRequestBuilder()
                        .withHttpMethod(HttpMethodName.GET)
                        .withResourcePath(path)
                        .withHeaders(headers, null)
                        .build();
                valid = (openSearchClient.execute(request).getStatus() == 200);
            }
            default -> valid = requestInfo.userIsAuthorized("USER");
        }
        return valid;
    }


    public boolean addUserWorkspace(AuthorizerResponse authorizerResponse) {
        var request = new OpenSearchRequestBuilder()
                .withHttpMethod(HttpMethodName.POST)
                .withResourcePath(WORKSPACE)
                .withBody(new ByteArrayInputStream(authorizerResponse.toString().getBytes()))
                .build();
           return (openSearchClient.execute(request).getStatus() == 201);

    }



}
