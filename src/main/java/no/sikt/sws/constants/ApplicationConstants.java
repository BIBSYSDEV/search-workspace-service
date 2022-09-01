package no.sikt.sws.constants;

import nva.commons.core.Environment;

public final class ApplicationConstants {

    public static final Environment ENVIRONMENT = new Environment();
    public static final String OPENSEARCH_ENDPOINT_ADDRESS = readOpenSearchEndpoint();

    private static String readOpenSearchEndpoint() {
        return ENVIRONMENT.readEnv("OPENSEARCH_ENDPOINT_ADDRESS");
    }

}
