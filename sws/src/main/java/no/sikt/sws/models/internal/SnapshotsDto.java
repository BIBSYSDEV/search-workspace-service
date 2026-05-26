package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Comparator;

public class SnapshotsDto {

    @JsonProperty
    public Collection<SnapshotDto> snapshots;


    public Long getLatestEpocTime() {
        return snapshots.stream()
            .max(Comparator.comparing(a -> a.epochTime))
            .orElseThrow().epochTime;
    }

}
