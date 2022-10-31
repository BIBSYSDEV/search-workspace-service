package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotDto {
    @JsonProperty("snapshot")
    String name;

    @JsonProperty("uuid")
    String uuid;

    @JsonProperty("end_time_in_millis")
    Long epochTime;

    public Long getEpochTime() {
        return epochTime;
    }

    public String getName() {
        return name;
    }
}
