package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.sws.models.opensearch.SnapshotRequestDto;
import no.sikt.sws.models.opensearch.SnapshotSettingsRequestDto;
import no.unit.nva.commons.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotSettingsRequestDtoTest {

    @Test
    public void testToString() throws JsonProcessingException {
        var settings = new SnapshotSettingsRequestDto("marinas-bucket", null, null, "/snapshots");
        var request = new SnapshotRequestDto("s3", settings);
        var jsonStr = JsonUtils.dtoObjectMapper.writeValueAsString(request);
        assertTrue(jsonStr.contains("marinas-bucket"));
    }

}