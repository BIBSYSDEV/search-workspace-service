package no.sikt.sws;

import nva.commons.apigateway.RequestInfo;
import org.junit.jupiter.api.Test;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestUtilTest {
    @Test
    void shouldGetWorkspaceFromSingle() {

        var scope = "https://api.sws.aws.sikt.no/scopes/workspace-sondre";
        var expectedWorkspace = "workspace-sondre";

        var reqest = new RequestInfo();
        injectClaim(reqest, scope);

        var actualWorkspace = RequestUtil.getWorkspace(reqest);
        assertEquals(expectedWorkspace, actualWorkspace);
    }

    @Test
    void shouldGetWorkspaceFromList() {

        var scope = "https://api.sws.aws.sikt.no/scopes/workspace https://api.sws.aws.sikt.no/scopes/workspace-sondre";
        var expectedWorkspace = "workspace-sondre";

        var reqest = new RequestInfo();
        injectClaim(reqest, scope);

        var actualWorkspace = RequestUtil.getWorkspace(reqest);
        assertEquals(expectedWorkspace, actualWorkspace);

    }

    private void injectClaim(RequestInfo requestInfo, String claim) {
        var claims = dtoObjectMapper.createObjectNode();
        var authorizer = dtoObjectMapper.createObjectNode();
        var requestContext = dtoObjectMapper.createObjectNode();
        claims.put("scope", claim);
        authorizer.set("claims", claims);
        requestContext.set("authorizer", authorizer);
        requestInfo.setRequestContext(requestContext);
    }
}
