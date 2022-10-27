package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.Snapshot;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;


public class SnapshotRoutineDeletionHandler extends ApiGatewayHandler<Void, String> {

    private static final Long fourteenDays = Long.valueOf(1_209_600_000);
    private static final Logger logger = LoggerFactory.getLogger(SnapshotRoutineDeletionHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotRoutineDeletionHandler() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo renquestInfo, Context context) throws ApiGatewayException {

        var nameOfSnapshotRepo = "initialsnapshot"; //TODO: hardcoded RegisterSnapshotHandler
        var snapshotRepoPathRequest = "_snapshot/" + nameOfSnapshotRepo;

        try {
            var allSnaps = returnAllSnaps(nameOfSnapshotRepo);
            var lastExistingSnapEpoch = deleteOldSnaps(allSnaps, snapshotRepoPathRequest);
            logger.info("The last(base) snapshot to restore:" + lastExistingSnapEpoch);
        } catch (Exception e) {
            throw new SearchException("Something went wrong with deleting outdated snapshots", e);
        }
        return "undefined";
    }

    protected String returnAllSnaps(String nameOfSnapshotRepo) throws ApiGatewayException {
        var snapshotGetAllRequests = "_snapshot/" + nameOfSnapshotRepo + "/_all";
        try {
            var response = openSearchClient.sendRequest(HttpMethodName.GET,
                    snapshotGetAllRequests,
                    null);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when listing all snapshots:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }

    }

    protected String deleteOldSnaps(String jsonStringOFAllSnaps, String snapshotRepoPathRequest)
            throws SearchException {

        var arrayOfSnapshots = new ArrayList<Snapshot>();
        JSONObject allSNapObject = new JSONObject(jsonStringOFAllSnaps);
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

        Collections.sort(arrayOfSnapshots, Snapshot.Comparators.SNAP_COMPARATOR_TIME);


        try {
            arrayOfSnapshots.stream()
                    .filter(item -> item.getEpochTime().getTime() > fourteenDays)
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
        var theNewestSnapshot = arrayOfSnapshots.get(arrayOfSnapshots.size() - 1).getName();
        return theNewestSnapshot;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }


}
