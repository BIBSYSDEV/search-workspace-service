package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.sikt.sws.models.internal.Snapshot;

import java.util.Collection;
import java.util.Map;

public class SnapshotsDto {

   @JsonProperty
   public Collection<SnapshotDto> snapshots;



}
