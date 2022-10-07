package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static no.sikt.sws.constants.ApplicationConstants.BACKUP_BUCKET_NAME;

public class SnapshotTakingLambda extends ApiGatewayHandler<Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(RegisterSnapshotRepoHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotTakingLambda() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        var timestamp = new Timestamp(System.currentTimeMillis()).toString();
        var snapshotRepoName = "initialsnapshot"; //TODO: hardcoded RegisterSnapshotHandler
        var createSnapshotRequest = "/snap" + timestamp;

        try {
            logger.info(BACKUP_BUCKET_NAME);
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                    "_snapshot/" + snapshotRepoName,
                    createSnapshotRequest);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when attempting to take snapshot:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}


