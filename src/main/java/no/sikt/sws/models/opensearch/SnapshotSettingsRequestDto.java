package no.sikt.sws.models.opensearch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotSettingsRequestDto {

    @JsonProperty("bucket")
    public String bucket;

    @JsonProperty("region")
    public String region;

    @JsonProperty("role_arn")
    public String roleArn;

    @JsonProperty("base_path")
    public String basePath;

    public SnapshotSettingsRequestDto(String bucket, String region, String roleArn, String basePath) {
        this.bucket = bucket;
        this.region = region;
        this.roleArn = roleArn;
        this.basePath = basePath;
    }
}
