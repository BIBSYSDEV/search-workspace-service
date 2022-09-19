package no.sikt.sws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.amazonaws.http.HttpMethodName.GET;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE;
import static no.sikt.sws.testutils.TestConstants.TEST_WORKSPACE_PREFIX;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class WorkspaceHandlerTest extends TestCase {


    private static final Context CONTEXT = new FakeContext();
    private ByteArrayOutputStream output;

    @InjectMocks
    WorkspaceHandler handler = new WorkspaceHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);

        this.output = new ByteArrayOutputStream();
    }

    @Test
    void callToOpenSearchMapsToCorrectResponse() throws IOException {
        String mockJson = "{\"hallo\": {\"field1\": \"somevalue\"}}";

        final OpenSearchResponse mockResponse = new OpenSearchResponse(200, mockJson);

        when(openSearchClient.sendRequest(GET, TEST_WORKSPACE_PREFIX + "*", null))
                .thenReturn(mockResponse);


        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(HttpMethod.GET.toString())
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .build();

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, WorkspaceResponse.class);

        assertThat(response.getStatusCode(), is(equalTo(HTTP_OK)));

        WorkspaceResponse workspaceResponse = response.getBodyObject(WorkspaceResponse.class);

        Assertions.assertNotNull(workspaceResponse.indexList);
        Assertions.assertNotNull(workspaceResponse.accountIdentifier);

    }
}
