package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;

import no.sikt.sws.exception.SearchException;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import software.amazon.awssdk.regions.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.BACKUP_BUCKET_NAME;

public class RegisterSnapshotRepoS3 {

    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);


    private final String bucketName = BACKUP_BUCKET_NAME;


    public OpenSearchClient openSearchClient = OpenSearchClient.passthroughClient();



    public OpenSearchResponse registerRepository() throws SearchException {
        JSONObject jsonSettings = new JSONObject();
        JSONObject jsonRepo = new JSONObject();
        jsonRepo.put("type", "s3");
        jsonSettings.put("bucket", bucketName);
        jsonSettings.put("base_path","/snapshots");
        jsonRepo.put("settings", jsonSettings);
        String jsonRequest = jsonRepo.toString();

        try {
            var response = openSearchClient.sendRequest(HttpMethodName.PUT, "", jsonRequest);
            logger.info("response-code:" + response.getStatus());
            return response;
        } catch (Exception e) {
            logger.error("Error when attempting to set snapshot repository:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


}
