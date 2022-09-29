package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;


import no.sikt.sws.exception.SearchException;
import no.sikt.sws.models.opensearch.SnapshotRequestDto;
import no.sikt.sws.models.opensearch.SnapshotSettingsRequestDto;
import no.unit.nva.commons.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.BACKUP_BUCKET_NAME;

public class RegisterSnapshotRepoS3 {

    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);
    public OpenSearchClient openSearchClient = new OpenSearchClient();

    public OpenSearchResponse registerRepository() throws SearchException {
        var settings = new SnapshotSettingsRequestDto(BACKUP_BUCKET_NAME, null, null, "/snapshots");
        var request = new SnapshotRequestDto("s3", settings);

        try {
            var requestStr = JsonUtils.dtoObjectMapper.writeValueAsString(request);
            var response = openSearchClient.sendRequest(HttpMethodName.PUT, "", requestStr);
            logger.info("response-code:" + response.getStatus());
            logger.info("response-body:" + response.getBody());
            return response;
        } catch (Exception e) {
            logger.error("Error when attempting to set snapshot repository:" + e.getMessage(), e);
            throw new SearchException(e.getMessage(), e);
        }
    }


}
