package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;


import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.opensearch.SnapshotRequestDto;
import no.sikt.sws.models.opensearch.SnapshotSettingsRequestDto;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.*;

public class SnapshotRegisterRepoHandler extends ApiGatewayHandler<Void, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotRegisterRepoHandler.class);
    public OpenSearchClient openSearchClient = OpenSearchClient.defaultClient();

    public SnapshotRegisterRepoHandler() {
        super(Void.class, new Environment());
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        // no op
    }

    @Override
    protected String processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        var settings = new SnapshotSettingsRequestDto(BACKUP_BUCKET_NAME, null, BACKUP_ROLE_ARN, "snapshots");
        var request = new SnapshotRequestDto("s3", settings);

        try {
            LOGGER.info(BACKUP_BUCKET_NAME);
            var requestStr = JsonUtils.dtoObjectMapper.writeValueAsString(request);
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                SNAPSHOT_REPO_PATH_REQUEST,
                    requestStr);
            LOGGER.info("response-code:" + response.getStatus());
            LOGGER.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("Error when attempting to set snapshot repository:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}
