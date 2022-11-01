package no.sikt.sws.testutils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.internal.TokenBucket;
import com.amazonaws.metrics.RequestMetricCollector;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import org.mockito.ArgumentMatcher;


public class FakeAwsClient extends AmazonHttpClient {

    private Request<Void> request;
    private HttpResponse response;

    public FakeAwsClient(Request<Void> request, HttpResponse response) {
        super(new ClientConfiguration());
        this.request = request;
        this.response = response;
    }

    @Override
    public RequestExecutionBuilder requestExecutionBuilder() {
        return new FakeAwsExecutionBuilder(this.request, this.response);
    }
}


