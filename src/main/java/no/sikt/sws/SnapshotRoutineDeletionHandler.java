package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.SnapshotsDto;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static no.sikt.sws.constants.ApplicationConstants.SNAPSHOT_REPO_PATH_REQUEST;


public class SnapshotRoutineDeletionHandler extends ApiGatewayHandler<Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotRoutineDeletionHandler.class);
    private static final Long FOURTEEN_DAYS =  14 * 24 * 60 * 60 * 1000L;
    public static final String SNAPSHOT_GET_ALL_REQUESTS = SNAPSHOT_REPO_PATH_REQUEST + "/_all";

    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotRoutineDeletionHandler() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo renquestInfo, Context context) throws ApiGatewayException {
        try {
            var allSnaps = returnAllSnaps();
            return deleteOldSnaps(allSnaps);
        } catch (Exception e) {
            throw new SearchException("Something went wrong with deleting outdated snapshots", e);
        }
    }

    protected SnapshotsDto returnAllSnaps() throws ApiGatewayException {
        try {
            var response = openSearchClient.sendRequest(HttpMethodName.GET,
                    SNAPSHOT_GET_ALL_REQUESTS,
                    null);
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            return objectMapper.readValue(response.getBody(), SnapshotsDto.class);
        } catch (Exception e) {
            logger.error("Error when listing all snapshots:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    protected String deleteOldSnaps(SnapshotsDto snapshotsDto)
            throws SearchException {
        var responses = new ArrayList<String>();
        var latest = snapshotsDto.getLatestEpocTime();
        try {
            snapshotsDto.snapshots.stream()
                .filter(item -> latest - item.getEpochTime() > FOURTEEN_DAYS)
                .forEach(snap -> {
                    var response = openSearchClient.sendRequest(HttpMethodName.DELETE,
                        SNAPSHOT_REPO_PATH_REQUEST + "/" + snap.getName(),
                            null);
                    logger.info("for the snapshot: " + snap.getName()
                            + " the response is: " + response.getStatus());
                    responses.add(response.getBody());
                });
            return String.join(",", responses);
        } catch (Exception e) {
            throw new SearchException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }

}
