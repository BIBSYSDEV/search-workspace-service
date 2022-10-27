package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;

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
    public Collection<String> failures;

    public ShardsDto() {
        super();
    }

    @Override
    @SuppressWarnings("unused")
    public ShardsDto stripper(String workspacePrefix) {
        var regex = toRegEx.apply(workspacePrefix);
        if (failures != null) {
            failures.forEach(failure ->
                failure = failure.replaceAll(regex,EMPTY_STRING));
        } else {
            logger.info(toString());
        }
        return this;
    }

}
