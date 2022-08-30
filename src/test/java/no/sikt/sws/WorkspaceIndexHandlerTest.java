package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import no.unit.nva.commons.json.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.WorkspaceIndexHandler.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void shouldGiveResponse() throws ApiGatewayException, IOException {

        var pathparams = Map.of(
                WORKSPACE_IDENTIFIER, "sondre",
                RESOURCE_IDENTIFIER, "index1/_mapping"
        );

        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(HttpMethod.PUT.toString())
                .withPathParameters(pathparams)
                .build();

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, IndexResponse.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));
    }

}