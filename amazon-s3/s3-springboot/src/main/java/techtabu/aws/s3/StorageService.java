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

    public void createLifecyclePolicy(String bucket, String prefix, Integer days) {

        Assert.hasText(bucket, "bucket name cannot by null or empty");
        Assert.hasText(prefix, "prefix cannot by null or empty");
        Assert.isTrue(prefix.endsWith("/"), "prefix must end with /");
        Assert.isTrue(days > 0, "days should be positive integer");

        log.info("Adding life cycle configuration to bucket {} for objects with prefix {} for days: {}", bucket, prefix, days);
        try {
            LifecycleRuleFilter filter = LifecycleRuleFilter.builder()
                    .prefix(prefix)
                    .build();

            LifecycleExpiration expiration = LifecycleExpiration.builder()
                    .days(days)
                    .build();

            LifecycleRule rule = LifecycleRule.builder()
                    .expiration(expiration)
                    .filter(filter)
                    .status(ExpirationStatus.ENABLED)
                    .build();

            BucketLifecycleConfiguration config = BucketLifecycleConfiguration.builder()
                    .rules(List.of(rule))
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
