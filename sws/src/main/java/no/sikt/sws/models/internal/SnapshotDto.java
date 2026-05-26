package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotDto {
    @JsonProperty("snapshot")
    public String name;

    @JsonProperty("uuid")
    public String uuid;

    @JsonProperty("end_time_in_millis")
    public Long epochTime;

    public Long getEpochTime() {
        return epochTime;
    }

    public String getName() {
        return name;
    }
}
