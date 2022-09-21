package no.sikt.sws;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;

import static software.amazon.awssdk.services.s3.model.HeadBucketRequest.*;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapshotOpenSearchCreation {

    ProfileCredentialsProvider credentialsProvider =
            ProfileCredentialsProvider.create();
    Region region = Region.EU_WEST_1;
    S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();

    PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();

}
