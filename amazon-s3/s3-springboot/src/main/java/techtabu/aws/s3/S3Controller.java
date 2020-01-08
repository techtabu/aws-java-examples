package techtabu.aws.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/s3-controller")
@Slf4j
public class S3Controller {

    S3Client s3Client;

    @PostConstruct
    public void createClient() {
        s3Client = S3Client.create();
    }

    @PostMapping("/bucket/create")
    public void createBucket(@RequestParam String bucketName) {

        CreateBucketResponse response = s3Client
                .createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName).build());
        log.info(response.toString());
    }

    @GetMapping("/bucket/getall")
    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        response.buckets().forEach(b -> {
            log.info("Bucket: {}", b.name());
        });

        return response.buckets().stream().map(b -> b.name()).collect(Collectors.toList());
    }
}
