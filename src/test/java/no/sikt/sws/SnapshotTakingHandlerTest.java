package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.stubs.FakeContext;
import no.unit.nva.testutils.HandlerRequestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE_SONDRE;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;

class SnapshotTakingHandlerTest {
    private static final Context CONTEXT = new FakeContext();
    @InjectMocks
    SnapshotTakingHandler handler = new SnapshotTakingHandler();

    @Mock
    OpenSearchClient openSearchClient;

    @Test
    void createSNapshotTest() throws IOException {
        var request = new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE_SONDRE)
                .build();

        var output = new ByteArrayOutputStream();
        handler.handleRequest(request, output, CONTEXT);
    }

}