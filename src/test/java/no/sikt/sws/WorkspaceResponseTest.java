package no.sikt.sws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class WorkspaceResponseTest {


    @Test
    void workspaceResponseMapsCorrectly() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String mockJson =
                "{\"account_identifier\":\"hei\",\"index_list\":{\"hallo\":{\"hade\":\"somevalue\"}}}";

        WorkspaceResponse workspaceObject = objectMapper.readValue(mockJson, WorkspaceResponse.class);
        String mappedString = objectMapper.valueToTree(workspaceObject).toString();
        assertThat(mappedString, is(equalTo(mockJson)));
    }


}
