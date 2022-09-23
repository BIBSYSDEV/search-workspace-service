package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;

import no.sikt.sws.exception.SearchException;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterSnapshotRepoS3 {

    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

    ProfileCredentialsProvider credentialsProvider =
            ProfileCredentialsProvider.create();
    Region region = Region.EU_WEST_1;
    String bucketName = ""; //
    S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();

    public OpenSearchClient openSearchClient = OpenSearchClient.passthroughClient();

    private String bucketName; //sws-indices-snapshot-100820817332-sandbox

    public OpenSearchResponse registerRepository() throws SearchException{
        JSONObject jsonSettings = new JSONObject();
        JSONObject jsonRepo = new JSONObject();
        jsonRepo.put("type", "s3");
        jsonSettings.put( "bucket", "");
        jsonSettings.put( "base_path","/snapshots");
        jsonRepo.put("settings", jsonSettings);
        String jsonRequest = jsonRepo.toString();

        try {
        var response = openSearchClient.sendRequest(HttpMethodName.PUT, "", jsonRequest);
        logger.info("response-code:" + response.getStatus());
        return response;
    }
        catch(Exception e){
            logger.error("Error when attemting to set snapshot repository:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


}
