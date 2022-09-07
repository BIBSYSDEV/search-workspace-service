package no.sikt.sws.builders;

import com.amazonaws.http.HttpMethodName;
import no.sikt.sws.OpenSearchRequest;
import nva.commons.core.JacocoGenerated;
import software.amazon.awssdk.utils.Validate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenSearchRequestBuilder {
    private static final String API_KEY_HEADER = "x-api-key";

    private HttpMethodName httpMethod;
    private String resourcePath;
    private InputStream body = null;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();

    public OpenSearchRequestBuilder withHttpMethod(HttpMethodName name) {
        httpMethod = name;
        return this;
    }

    public OpenSearchRequestBuilder withResourcePath(String path) {
        resourcePath = path;
        return this;
    }

    public OpenSearchRequestBuilder withBody(InputStream content) {
        this.body = content;
        return this;
    }

    public OpenSearchRequestBuilder withHeaders(Map<String, String> headers, String apiKey) {
        this.headers = buildRequestHeaders(headers,apiKey);
        return this;
    }

    public OpenSearchRequestBuilder withParameters(Map<String,List<String>> parameters) {
        if (parameters != null) {
            this.parameters = parameters;
        }
        return this;
    }

    public OpenSearchRequest<Void> build() {
        Validate.notNull(httpMethod, "HTTP method");
        Validate.notEmpty(resourcePath, "Resource path");
        return new OpenSearchRequest<>(httpMethod, resourcePath, body, headers, parameters);
    }

    @JacocoGenerated
    private Map<String, String> buildRequestHeaders(Map<String, String> headers, String apiKey) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (apiKey != null) {
            final Map<String, String> headersWithApiKey = new HashMap<>();
            headersWithApiKey.putAll(headers);
            headersWithApiKey.put(API_KEY_HEADER, apiKey);
            return headersWithApiKey;
        } else {
            return headers;
        }
    }

}
