package no.sikt.sws;

import com.amazonaws.Request;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.mockito.ArgumentMatcher;

@SuppressWarnings({"PMD.OnlyOneReturn"})
public class RequestMatcher implements ArgumentMatcher<Request> {

  private final Request sourceRequest;

  public RequestMatcher(Request sourceRequest) {
    this.sourceRequest = sourceRequest;
  }

  @Override
  public boolean matches(Request request) {
    if (!sourceRequest.getEndpoint().equals(request.getEndpoint())) {
      return false;
    }
    if (!sourceRequest.getParameters().equals(request.getParameters())) {
      return false;
    }

    if (sourceRequest.getContent() != null) {
      var expectedContent =
          new BufferedReader(
                  new InputStreamReader(sourceRequest.getContent(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));

      var actualContent =
          new BufferedReader(new InputStreamReader(request.getContent(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));

      if (!expectedContent.equals(actualContent)) {
        return false;
      }
    }
    return sourceRequest.getEndpoint().equals(request.getEndpoint());
  }
}
