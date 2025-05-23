package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sikt.sws.models.internal.WorkspaceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static no.sikt.sws.constants.ApplicationConstants.API_GATEWAY_URL;
import static no.sikt.sws.testutils.Constants.OPEN_SEARCH_INDEX_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkspaceResponseTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void workspaceResponseMapsCorrectly() throws JsonProcessingException {

        String responseJson =
                    "{\n"
                   + "   \"account_identifier\":\"hei\",\n"
                   + "   \"index_list\":{\n"
                   + "      \"hallo\":{\n"
                   + "         \"settings\":{\n"
                   + "            \"index\":{\n"
                   + "               \"number_of_replicas\":\"1\"\n"
                   + "            }\n"
                   + "         }\n"
                   + "      }\n"
                   + "   }\n"
                   + "}";
        String settingsJson = "{\"index\":{\"number_of_replicas\":\"1\"}}";

        WorkspaceResponse workspaceObject = OBJECT_MAPPER.readValue(responseJson, WorkspaceResponse.class);

        assertEquals("hei", workspaceObject.accountIdentifier);
        assertEquals(settingsJson, workspaceObject.indexList.get("hallo").settings.toString());
        assertNull(workspaceObject.createIndexLink);
    }

    @Test
    void fromJsonValueNotEmpty() throws JsonProcessingException {
        var workspaceResponse = WorkspaceResponse.fromValues("mockWorkSpace", OPEN_SEARCH_INDEX_LIST);

        Assertions.assertTrue(workspaceResponse.indexList.containsKey("hallo"));
        assertEquals(1, workspaceResponse.indexList.size());

        var index = workspaceResponse.indexList.entrySet().iterator().next();

        assertEquals(API_GATEWAY_URL + "/" + "hallo", index.getValue().indexLink);
    }

    @Test
    void fromJsonValueEmpty() throws JsonProcessingException {
        var indexList = "{ }";

        var workspaceResponse = WorkspaceResponse.fromValues("mockWorkSpace", indexList);

        Assertions.assertTrue(workspaceResponse.indexList.isEmpty());
    }

    @Test
    void addHypperLinkToWorkspaceResponse() throws JsonProcessingException {
        var workspaceResponse = WorkspaceResponse.fromValues("mockWorkSpace", "{ }");
        assertEquals(API_GATEWAY_URL + "/index_name", workspaceResponse.createIndexLink);
    }

}
