package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotRequestDto {
    @JsonProperty("type")
    public String type;

    @JsonProperty("settings")
    public SnapshotSettingsRequestDto settings;

    public SnapshotRequestDto(String type, SnapshotSettingsRequestDto settings) {
        this.type = type;
        this.settings = settings;
    }
}
