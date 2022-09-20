package no.sikt.sws.constants;

import nva.commons.core.Environment;

public final class ApplicationConstants {

    public static final Environment ENVIRONMENT = new Environment();
    public static final String OPENSEARCH_ENDPOINT_PROTOCOL = readOpenSearchEndpointProtocol();
    public static final String OPENSEARCH_ENDPOINT_ADDRESS = readOpenSearchEndpointAddress();
    public static final String SCOPE_IDENTIFIER = readScopeIdentifier();
    public static final String API_GATEWAY_URL = readApiGatewayEndpoint();

    public static final String ELASTICSEARCH_REGION = readElasticSearchRegion();

    private static String readOpenSearchEndpointAddress() {
        return ENVIRONMENT.readEnv("OPENSEARCH_ENDPOINT_ADDRESS");
    }

    private static String readOpenSearchEndpointProtocol() {
        return ENVIRONMENT.readEnv("OPENSEARCH_ENDPOINT_PROTOCOL");
    }

    private static String readElasticSearchRegion() {
        return ENVIRONMENT.readEnv("ELASTICSEARCH_REGION");
    }

    private static String readScopeIdentifier() {
        return ENVIRONMENT.readEnv("SCOPE_IDENTIFIER");
    }

    private static String readApiGatewayEndpoint() {
        return ENVIRONMENT.readEnv("API_GATEWAY_URL");
    }


}
