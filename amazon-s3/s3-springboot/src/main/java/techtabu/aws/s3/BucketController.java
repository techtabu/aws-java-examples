package techtabu.aws.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
    public void createBucket(@RequestParam(value = "name") String bucketName) {

        log.info("Creating bucket: {}", bucketName);
        CreateBucketResponse response = s3Client
                .createBucket(CreateBucketRequest.builder()
                                .bucket(bucketName).build());

        log.info(response.toString());
    }

    @GetMapping
    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        response.buckets().forEach(b -> {
            log.info("Bucket: {}", b.name());
        });

        return response.buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    @DeleteMapping
    public void deleteBucket(@RequestParam(value = "name") String bucketName) {
        log.info("Trying to delete bucket: {}", bucketName);
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        s3Client.deleteBucket(deleteBucketRequest);
    }


    // List Objects
    @GetMapping("/{bucket-name}")
    public List<String> getAllObjectsFromBucket(@PathVariable("bucket-name") String bucketName) {
        ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
        List<S3Object> objects = s3Client.listObjects(request).contents();
        List<String> files = new ArrayList<>();
        objects.forEach(o -> {
            log.info("Name: {}", o.key());
            files.add(o.key());
        });

        return files;
    }
}
