package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Date;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.SNAPSHOT_REPO_PATH_REQUEST;


public class SnapshotTakingHandler extends ApiGatewayHandler<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotTakingHandler.class);
    public OpenSearchClient openSearchClient = OpenSearchClient.defaultClient();

    public SnapshotTakingHandler() {
        super(Void.class, new Environment());
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        // no op
    }

    @Override
    protected String processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {

        var timestamp = String.valueOf(new Date().getTime());
        var createSnapshotName = "snap" + timestamp;

        try {
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                SNAPSHOT_REPO_PATH_REQUEST + "/" + createSnapshotName,
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
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}


