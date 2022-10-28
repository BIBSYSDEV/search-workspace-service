package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class SnapshotsDto {

    @JsonProperty
    public Collection<SnapshotDto> snapshots;


}
