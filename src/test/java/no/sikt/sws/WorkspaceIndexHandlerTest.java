package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.WorkspaceIndexHandler.RESOURCE_IDENTIFIER;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class WorkspaceIndexHandlerTest extends TestCase {

    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;
    private WorkspaceIndexHandler handler;

    @BeforeEach
    public void setup() {
        this.output = new ByteArrayOutputStream();
        this.handler = new WorkspaceIndexHandler();
    }

    @Test
    @Disabled("Disabled until we have mocking of opensearch")
    void shouldGiveResponse() throws IOException {

        var pathparams = Map.of(
                RESOURCE_IDENTIFIER, "index1/_mapping"
        );

        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(HttpMethod.PUT.toString())
                .withPathParameters(pathparams)
                .withAuthorizerClaim(SCOPE_CLAIM,"https://api.sws.unit.no/scopes/workspace-sondre")
                .build();

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, IndexResponse.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

}