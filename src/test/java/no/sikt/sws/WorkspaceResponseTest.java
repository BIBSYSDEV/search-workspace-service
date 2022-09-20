package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nva.commons.core.Environment;
import org.junit.jupiter.api.Test;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WorkspaceResponseTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void workspaceResponseMapsCorrectly() throws JsonProcessingException {

        String mockJson =
                "{\"account_identifier\":\"hei\",\"index_list\":{\"hallo\":{\"hade\":\"somevalue\"}}}";

        WorkspaceResponse workspaceObject = objectMapper.readValue(mockJson, WorkspaceResponse.class);

        assertEquals("hei", workspaceObject.accountIdentifier);
        assertEquals("{\"hallo\":{\"hade\":\"somevalue\"}}", workspaceObject.indexList.toString());
        assertNull(workspaceObject.createIndexLink);
    }

    @Test
    void addHypperLinkToWorkspaceResponseIfEmptyIndexList() throws JsonProcessingException {
        var workspaceResponse = WorkspaceResponse.fromValues("mockWorkSpace", "");

        assertEquals("", workspaceResponse.indexList.toString());

        assertEquals(API_GATEWAY_URL + "/index_name", workspaceResponse.createIndexLink);
    }


}
