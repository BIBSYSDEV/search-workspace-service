package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.Response;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.TestCase;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.amazonaws.http.HttpMethodName.PUT;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.WorkspaceIndexHandler.RESOURCE_IDENTIFIER;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkspaceIndexHandlerTest extends TestCase {

    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;

    @InjectMocks
    WorkspaceIndexHandler handler = new WorkspaceIndexHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @BeforeAll
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        final HttpResponse mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.getStatusCode())
                .thenReturn(200);

        final OpenSearchResponse response = new OpenSearchResponse(mockHttpResponse);

        when(openSearchClient.sendRequest(PUT, "localhost:3000/workspace-sondre-index1/_mapping"))
                .thenReturn(response);
    }

    @BeforeEach
    public void beforeEach() {
        this.output = new ByteArrayOutputStream();
    }


    @Test
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