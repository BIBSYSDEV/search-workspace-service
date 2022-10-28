package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.SnapshotsDto;
import no.sikt.sws.models.internal.Snapshot;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class SnapshotRoutineDeletionHandler extends ApiGatewayHandler<Void, String> {

    private static final Long fourteenDays = Long.valueOf(1_209_600_000);
    private static final Logger logger = LoggerFactory.getLogger(SnapshotRoutineDeletionHandler.class);
    public static final String INITIALSNAPSHOT = "initialsnapshot";
    public static final String SNAPSHOT_REPO_PATH_REQUEST = "_snapshot/" + INITIALSNAPSHOT;
    public static final String SNAPSHOT_GET_ALL_REQUESTS = SNAPSHOT_REPO_PATH_REQUEST + "/_all";
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotRoutineDeletionHandler() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo renquestInfo, Context context) throws ApiGatewayException {



        try {
            var allSnaps = returnAllSnaps();
            var lastExistingSnapEpoch = deleteOldSnaps(allSnaps, SNAPSHOT_REPO_PATH_REQUEST);
            logger.info("The last(base) snapshot to restore:" + lastExistingSnapEpoch);
        } catch (Exception e) {
            throw new SearchException("Something went wrong with deleting outdated snapshots", e);
        }
        return "undefined";
    }

    protected SnapshotsDto returnAllSnaps() throws ApiGatewayException {
        try {
            var response = openSearchClient.sendRequest(HttpMethodName.GET,
                    SNAPSHOT_GET_ALL_REQUESTS,
                    null);
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            var returnValue =  objectMapper.readValue(response.getBody(), SnapshotsDto.class);
            return returnValue;
        } catch (Exception e) {
            logger.error("Error when listing all snapshots:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    protected String deleteOldSnaps(SnapshotsDto snapshotsDto, String snapshotRepoPathRequest)
            throws SearchException {

/*
        var arrayOfSnapshots = new ArrayList<Snapshot>();
        JSONObject allSNapObject = new JSONObject(snapshotsDto);
        JSONArray snapshots = allSNapObject.getJSONArray("snapshots");

        for (int i = 0; i < snapshots.length(); i++) {
            JSONObject snapshotEntry = snapshots.getJSONObject(i);
            var snapshotRow = new Snapshot();
            String nameOfTimeKey = "end_time_in_millis";
            snapshotRow.setName(snapshotEntry.getString("snapshot"));
            snapshotRow.setEpochTime(snapshotEntry.getLong(nameOfTimeKey));
            arrayOfSnapshots.add(snapshotRow);


        }
        int numberOfSnapshots = arrayOfSnapshots.size();
        logger.info("number of retrieved snapshots: " + numberOfSnapshots);
*/

        try {
            snapshotsDto.snapshots.stream()
                    .filter(item -> item.getEpochTime() > fourteenDays)
                    .forEach(snap -> {

                        var response = openSearchClient.sendRequest(HttpMethodName.DELETE,
                                snapshotRepoPathRequest + "/" + snap.getName(),
                                null);
                        logger.info("for the snapshot: " + snap.getName()
                                + " the response is: " + response.getStatus());

                    });
        } catch (Exception e) {
            throw new SearchException(e.getMessage(), e);
        }
        return "unvalidated";
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }


}
