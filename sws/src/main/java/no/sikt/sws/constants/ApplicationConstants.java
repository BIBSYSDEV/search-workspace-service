package no.sikt.sws.constants;

import nva.commons.core.Environment;

public final class ApplicationConstants {



    public static final Environment ENVIRONMENT = new Environment();
    public static final String API_GATEWAY_URL = readApiGatewayEndpoint();
    public static final String BACKEND_SCOPE_RESOURCE_SERVER_NAME = "Scopes for backend";
    public static final String BACKUP_BUCKET_NAME = readBackupBucketName();
    public static final String BACKUP_ROLE_ARN = readBackupRoleArn();
    public static final String SNAPSHOT_REPO_PATH_REQUEST = "_snapshot/snapshots";
    public static final String ELASTICSEARCH_REGION = readElasticSearchRegion();
    public static final String EMPTY_STRING = "";
    public static final String OPENSEARCH_ENDPOINT_ADDRESS = readOpenSearchEndpointAddress();
    public static final String OPENSEARCH_ENDPOINT_PROTOCOL = readOpenSearchEndpointProtocol();
    public static final String REQUIRED_PARAMETER_IS_NULL = "required parameter is null -> ";
    public static final String SCOPE_IDENTIFIER = readScopeIdentifier();
    public static final String USER_POOL_NAME = "SWSUserPool";
    public static final String WORKSPACE_PREFIX = "[workspacePrefix]";

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

    private static String readBackupBucketName() {
        return ENVIRONMENT.readEnv("BACKUP_BUCKET_NAME");
    }

    private static String readBackupRoleArn() {
        return ENVIRONMENT.readEnv("BACKUP_ROLE_ARN");
    }

}
