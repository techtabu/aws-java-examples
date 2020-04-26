package techtabu.aws.s3;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buckets")
@Slf4j
public class BucketController {

    S3Client s3Client;

    @PostConstruct
    public void createClient() {
        s3Client = S3Client.create();
    }

    @PostMapping
    @ApiOperation(value = "Create a new bucket with given name")
    public void createBucket(@RequestParam(value = "name") String bucketName) {

        log.info("Creating bucket: {}", bucketName);
        CreateBucketResponse response = s3Client
                .createBucket(CreateBucketRequest.builder()
                                .bucket(bucketName).build());


        log.info(response.toString());
    }

    @GetMapping
    @ApiOperation(value = "Get all the buckets in S3", response = List.class)
    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        response.buckets().forEach(b -> {
            log.info("Bucket: {}", b.name());
        });

        return response.buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    @DeleteMapping
    @ApiOperation(value = "Deletes the bucket with given name if it is empty")
    public void deleteBucket(@RequestParam(value = "name") String bucketName) {
        log.info("Trying to delete bucket: {}", bucketName);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3Client.deleteBucket(deleteBucketRequest);
    }
}
