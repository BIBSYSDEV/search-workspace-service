package no.sikt.sws;

import no.sikt.sws.constants.ApplicationConstants;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

import static no.sikt.sws.constants.ApplicationConstants.OPENSEARCH_ENDPOINT_ADDRESS;

public class RestClientOpenSearch {


/*
    RestClientBuilder builder = RestClient.builder(
            HttpHost.create(OPENSEARCH_ENDPOINT_ADDRESS))
            .setHttpClientConfigCallback(
                    return new HttpClientBuilder().SetDefaultCredentialsProvider(credentialsProvider);
            );
    RestHighLevelClient client = new RestHighLevelClient(builder);
    
 */
}
