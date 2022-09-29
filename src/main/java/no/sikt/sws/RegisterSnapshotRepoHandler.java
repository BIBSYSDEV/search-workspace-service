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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.BACKUP_BUCKET_NAME;

public class RegisterSnapshotRepoHandler extends ApiGatewayHandler<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public RegisterSnapshotRepoHandler() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        var settings = new SnapshotSettingsRequestDto(BACKUP_BUCKET_NAME, null, null, "/snapshots");
        var request = new SnapshotRequestDto("s3", settings);

        try {
            var requestStr = JsonUtils.dtoObjectMapper.writeValueAsString(request);
            var snapshotRepoName = "initial-snapshot";
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                    "/_snapshot/" + snapshotRepoName,
                    requestStr);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when attempting to set snapshot repository:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}
