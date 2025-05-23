package no.sikt.sws;

import nva.commons.apigateway.RequestInfo;
import org.junit.jupiter.api.Test;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static nva.commons.core.attempt.Try.attempt;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestUtilTest {
    @Test
    void shouldGetWorkspaceFromSingle() {

        var scope = "https://api.sws.aws.sikt.no/scopes/workspace-sondre";
        var expectedWorkspace = "workspace-sondre";

        var reqest = getRequestInfo();
        injectClaim(reqest, scope);

        var actualWorkspace = RequestUtil.getWorkspace(reqest);
        assertEquals(expectedWorkspace, actualWorkspace);
    }

    private static RequestInfo getRequestInfo() {
        return attempt(() -> RequestInfo.fromString("{}")).orElseThrow();
    }

    @Test
    void shouldGetWorkspaceFromList() {

        var scope = "https://api.sws.aws.sikt.no/scopes/workspace https://api.sws.aws.sikt.no/scopes/workspace-sondre";
        var expectedWorkspace = "workspace-sondre";

        var reqest = getRequestInfo();
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
