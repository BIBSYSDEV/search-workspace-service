package no.sikt.sws;

import static com.amazonaws.http.HttpMethodName.PUT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import java.nio.file.Path;
import no.sikt.sws.models.opensearch.OpenSearchResponse;
import no.unit.nva.stubs.FakeContext;
import nva.commons.core.ioutils.IoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ClusterSettingsHandlerTest {

  private static final Context CONTEXT = new FakeContext();
  private static final String ACKNOWLEDGED_RESPONSE = "{\"acknowledged\":true}";
  private static final String FORBIDDEN_RESPONSE = "{\"error\":\"forbidden\"}";
  private static final String CLUSTER_SETTINGS_PATH = "_cluster/settings";
  private static final String CLUSTER_SETTINGS =
      IoUtils.stringFromResources(Path.of("cluster_settings.json"));

  @Mock private OpenSearchClient openSearchClient;
  private ClusterSettingsHandler handler;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
    handler = new ClusterSettingsHandler(openSearchClient);
  }

  @Test
  void shouldReturnResponseBodyWhenClusterSettingsAreApplied() {
    var response = new OpenSearchResponse(HTTP_OK, ACKNOWLEDGED_RESPONSE);
    when(openSearchClient.sendRequest(PUT, CLUSTER_SETTINGS_PATH, CLUSTER_SETTINGS))
        .thenReturn(response);

    var result = handler.handleRequest(null, CONTEXT);

    assertEquals(ACKNOWLEDGED_RESPONSE, result);
  }

  @Test
  void shouldThrowWhenApplyingClusterSettingsFails() {
    var response = new OpenSearchResponse(HTTP_FORBIDDEN, FORBIDDEN_RESPONSE);
    when(openSearchClient.sendRequest(PUT, CLUSTER_SETTINGS_PATH, CLUSTER_SETTINGS))
        .thenReturn(response);

    var exception =
        assertThrows(IllegalStateException.class, () -> handler.handleRequest(null, CONTEXT));
    assertTrue(exception.getMessage().contains("Failed to apply cluster settings"));
  }
}
