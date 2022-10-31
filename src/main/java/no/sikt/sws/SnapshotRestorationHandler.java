package no.sikt.sws;


import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.SnapshotToRestoreDto;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nva.commons.apigateway.ApiGatewayHandler;

import static no.sikt.sws.constants.ApplicationConstants.SNAPSHOT_REPO_PATH_REQUEST;


public class SnapshotRestorationHandler extends ApiGatewayHandler<SnapshotToRestoreDto, String> {


    private static final Logger logger = LoggerFactory.getLogger(SnapshotRestorationHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotRestorationHandler() {
        super(SnapshotToRestoreDto.class);
    }


    @Override
    protected String processInput(SnapshotToRestoreDto input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        var snapshotRepoPathRequest = SNAPSHOT_REPO_PATH_REQUEST + "/" + input.snapshotName + "/_restore";
        try {
            var response = openSearchClient.sendRequest(HttpMethodName.POST,
                    snapshotRepoPathRequest,
                    null);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when attempting to take snapshot:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


    @Override
    protected Integer getSuccessStatusCode(SnapshotToRestoreDto input, String output) {
        return 200;
    }
}
