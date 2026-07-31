package no.sikt.sws;

import static java.net.HttpURLConnection.HTTP_OK;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.nio.file.Path;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.ioutils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterSettingsHandler implements RequestHandler<Object, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClusterSettingsHandler.class);
  private static final String CLUSTER_SETTINGS_PATH = "_cluster/settings";
  private static final String CLUSTER_SETTINGS =
      IoUtils.stringFromResources(Path.of("cluster_settings.json"));
  private final OpenSearchClient openSearchClient;

  @JacocoGenerated
  public ClusterSettingsHandler() {
    this(OpenSearchClient.defaultClient());
  }

  public ClusterSettingsHandler(OpenSearchClient openSearchClient) {
    this.openSearchClient = openSearchClient;
  }

  @Override
  public String handleRequest(Object input, Context context) {
    LOGGER.info("Applying cluster settings: {}", CLUSTER_SETTINGS);
    var response =
        openSearchClient.sendRequest(HttpMethodName.PUT, CLUSTER_SETTINGS_PATH, CLUSTER_SETTINGS);
    LOGGER.info("Response: {} - {}", response.getStatus(), response.getBody());

    if (response.getStatus() != HTTP_OK) {
      throw new IllegalStateException("Failed to apply cluster settings");
    }
    return response.getBody();
  }
}
