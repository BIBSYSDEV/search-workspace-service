package no.sikt.sws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.http.HttpMethodName;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;

public class OpenSearchRequest<T> extends DefaultRequest<T> {

    public OpenSearchRequest(HttpMethodName httpMethod, String resourcePath,
                                    InputStream body, Map<String, String> headers,
                                    Map<String, List<String>> parameters) {
        super("es");
        this.setHttpMethod(httpMethod);
        this.setResourcePath(resourcePath);
        this.setContent(body);
        this.setHeaders(headers);
        this.setParameters(parameters);
        this.setEndpoint(URI.create("https://" + OPENSEARCH_ENDPOINT_ADDRESS));
    }

}
