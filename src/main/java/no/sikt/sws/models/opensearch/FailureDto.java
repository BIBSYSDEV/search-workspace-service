package no.sikt.sws.models.opensearch;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureDto extends Dto {
    @JsonProperty("shard")
    public Number shard;

    @JsonProperty("index")
    public String index;

    @JsonProperty("node")
    public String node;

    @JsonProperty("reason")
    public ReasonDto reason;

    public FailureDto() {
        super();
    }

    @Override
    public FailureDto stripper(String workspacePrefix) {
        index = index.replaceAll(workspacePrefix + "-", EMPTY_STRING);
        return this;
    }

    private class ReasonDto extends Dto {
        @JsonProperty("type")
        public String type;

        @JsonProperty("reason")
        public String reason;

        public ReasonDto() {
            super();
        }

        @Override
        Dto stripper(String workspacePrefix) {
            return this;
        }
    }
}
