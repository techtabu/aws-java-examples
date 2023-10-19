package techtabu.aws.s3;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TechTabu
 */

@Service
@Slf4j
public class StorageService {

    S3Client s3Client;

    @PostConstruct
    public void createClient() {
        s3Client = S3Client.create();
    }

    public void createBucket(String bucketName) {

        Assert.hasText(bucketName, "bucket name cannot by null or empty");
        log.info("Creating bucket: {}", bucketName);
        CreateBucketResponse response = s3Client
                .createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName).build());

        log.info(response.toString());
    }

    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        response.buckets().forEach(b -> {
            log.info("Bucket: {}", b.name());
        });

        return response.buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    public void deleteBucket(String bucketName) {
        Assert.hasText(bucketName, "bucket name cannot by null or empty");
        log.info("Trying to delete bucket: {}", bucketName);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3Client.deleteBucket(deleteBucketRequest);
    }

    public List<String> getAllObjectsFromBucket(String bucketName) {
        Assert.hasText(bucketName, "bucket name cannot by null or empty");
        ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
        List<S3Object> objects = s3Client.listObjects(request).contents();
        List<String> files = new ArrayList<>();
        objects.forEach(o -> {
            log.info("Name: {}", o.key());
            files.add(o.key());
        });

        return files;
    }

    public void createFolder(String bucket, String folder) {

        Assert.hasText(bucket, "bucket name cannot by null or empty");
        Assert.hasText(folder, "folder name cannot by null or empty");
        Assert.isTrue(folder.endsWith("/"), "folder name must end with /");

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(folder)
                .build();
        s3Client.putObject(request, RequestBody.empty());
    }

    public void createLifecyclePolicy(String bucket) {

        Assert.hasText(bucket, "bucket name cannot by null or empty");

        log.info("Adding life cycle configuration to bucket {}", bucket);
        try {
            LifecycleRuleFilter filter1 = LifecycleRuleFilter.builder()
                    .prefix("daily/")
                    .build();

            LifecycleExpiration expiration1 = LifecycleExpiration.builder()
                    .days(1)
                    .build();

            LifecycleRule rule1 = LifecycleRule.builder()
                    .id("one-day-delete-by-prefix-rule")
                    .expiration(expiration1)
                    .filter(filter1)
                    .status(ExpirationStatus.ENABLED)
                    .build();

            LifecycleRuleFilter filter2 = LifecycleRuleFilter.builder()
                    .tag(Tag.builder().key("retention").value("one-day").build())
                    .build();

            LifecycleExpiration expiration2 = LifecycleExpiration.builder()
                    .days(1)
                    .build();

            LifecycleRule rule2 = LifecycleRule.builder()
                    .id("one-day-delete-by-tag-rule")
                    .expiration(expiration2)
                    .filter(filter2)
                    .status(ExpirationStatus.ENABLED)
                    .build();


            BucketLifecycleConfiguration config = BucketLifecycleConfiguration.builder()
                    .rules(List.of(rule1, rule2))
                    .build();

            PutBucketLifecycleConfigurationRequest request = PutBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucket)
                    .lifecycleConfiguration(config)
                    .build();

            PutBucketLifecycleConfigurationResponse response = s3Client.putBucketLifecycleConfiguration(request);
            log.info("Life cycle configuration added {}", response.toString());
        } catch (S3Exception e) {
            log.error("Error adding lifecycle configuration", e);
        }


    }
}
