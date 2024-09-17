package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Objects;

public class ShardsDto extends Dto {

    @JsonProperty("total")
    public Number total;

    @JsonProperty("successful")
    public Number successful;

    @JsonProperty("skipped")
    public Number skipped;

    @JsonProperty("failed")
    public Number failed;

    @JsonProperty("failures")
    public Collection<FailureDto> failures;

    public ShardsDto() {
        super();
    }

    @Override
    @SuppressWarnings("unused")
    public ShardsDto stripper(String workspacePrefix) {
        if (Objects.nonNull(this.failures)) {
            this.failures.forEach(failureDto -> failureDto.stripper(workspacePrefix));
        }
        return this;
    }

}
