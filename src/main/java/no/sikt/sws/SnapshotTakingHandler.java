package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import no.sikt.sws.exception.SearchException;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SnapshotTakingHandler extends ApiGatewayHandler<Void, String> {

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
        var snapshotGetAllRequests = "_snapshot/" + nameOfSnapshotRepo+ "/_all";
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
    protected String deleteOldSnaps(String jsonString) {
        var input = new JSONObject();
        var array = new ArrayList<Array>();
        Object[] row = new Object[2];

        JSONObject allSNapObject = new JSONObject(jsonString);
        JSONArray snapshots = allSNapObject.getJSONArray("snapshots");

        for (int i = 0; i< snapshots.length(); i++){
            JSONObject snapshotEntry = snapshots.getJSONObject(i);
            row[0] = snapshotEntry.getString("name");
            row[1] = snapshotEntry.getInt("end_time_in_millis");
            //ToDo: make check for millis empty
        }
        return "unvalidated";
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, String output) {
        return 200;
    }
}


