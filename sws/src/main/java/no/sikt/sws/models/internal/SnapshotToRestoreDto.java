package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotToRestoreDto {
    @JsonProperty("snapshotName")
    public String snapshotName;
}
