package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.internal.Snapshot;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SnapshotTakingHandler extends ApiGatewayHandler<Void, String> {
    private static final Integer sevenDaysInEpoch = 604_800_000;
    private static final Logger logger = LoggerFactory.getLogger(SnapshotTakingHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public SnapshotTakingHandler() {
        super(Void.class);
    }

    @Override
    protected String processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        var timestamp = String.valueOf(new Date().getTime());
        var nameOfSnapshotRepo = "initialsnapshot"; //TODO: hardcoded RegisterSnapshotHandler
        var snapshotRepoPathRequest = "_snapshot/" + nameOfSnapshotRepo;
        var createSnapshotName = "snap" + timestamp;

        try {
            var allSnaps = returnAllSnaps(nameOfSnapshotRepo);
            var lastExistingSnapEpoch = deleteOldSnaps(allSnaps, snapshotRepoPathRequest);
            logger.error("The last(base) snapshot to restore:"+lastExistingSnapEpoch);
        }
        catch (Exception e){
            throw new SearchException("Something went wrong with deleting outdated snapshots", e);
        }

        try {
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                    snapshotRepoPathRequest + "/" + createSnapshotName,
                    null);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when attempting to take snapshot:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }

    protected String returnAllSnaps(String nameOfSnapshotRepo) throws ApiGatewayException {
        var snapshotGetAllRequests = "_snapshot/" + nameOfSnapshotRepo + "/_all";
        try {
            var response = openSearchClient.sendRequest(HttpMethodName.PUT,
                    snapshotGetAllRequests,
                    null);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error when attempting to take snapshot:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }

    }

    protected String deleteOldSnaps(String jsonString, String snapshotRepoPathRequest) throws SearchException {

        var arrayOfSnapshots = new ArrayList<Snapshot>();


        JSONObject allSNapObject = new JSONObject(jsonString);
        JSONArray snapshots = allSNapObject.getJSONArray("snapshots");

        for (int i = 0; i < snapshots.length(); i++) {
            JSONObject snapshotEntry = snapshots.getJSONObject(i);
            var snapshotRow = new Snapshot();
            snapshotRow.setName(snapshotEntry.getString("name"));
            if (snapshotEntry.getInt("end_time_in_millis") != 0) {
                try {
                    snapshotRow.setEpochTime(snapshotEntry.getInt("end_time_in_millis"));
                } catch (Exception e) {
                    throw new SearchException(e.getMessage(), e);
                }
            }
            arrayOfSnapshots.add(snapshotRow);
        }
        int numberOfSnapshots = arrayOfSnapshots.size();
        logger.info("number of retrieved snapshots: " + numberOfSnapshots);

        Collections.sort(arrayOfSnapshots, Comparator.comparingInt(Snapshot::getEpochTime));
        Collections.reverse(arrayOfSnapshots);
        Integer counter = 1;
        while ((arrayOfSnapshots.get(0).getEpochTime() - arrayOfSnapshots.get(counter).getEpochTime())
                < sevenDaysInEpoch) {
            counter++;
        }

        for (int i = numberOfSnapshots - 1; i >= counter; i--) {
            arrayOfSnapshots.remove(i);
        }
        logger.info("number of retrieved snapshots: " + arrayOfSnapshots.size());
        for (int i = 0; i < arrayOfSnapshots.size(); i++) {
            try {
                var response = openSearchClient.sendRequest(HttpMethodName.DELETE,
                        snapshotRepoPathRequest + "/" + arrayOfSnapshots.get(i).getName(),
                        null);
                logger.info("for the snapshot: " + arrayOfSnapshots.get(i).getName()
                        + " the response is: " + response.getStatus());
            } catch (Exception e) {
                throw new SearchException(e.getMessage(), e);
            }
        }
        var lastExistingSnapEpoch = arrayOfSnapshots.get(0).getName();
        return lastExistingSnapEpoch;
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}


