package techtabu.aws.s3;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buckets")
@Slf4j
public class BucketController {

    private final StorageService storageService;

    public BucketController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "create new bucket for the given name")
    @PostMapping
    public void createBucket(@RequestParam(value = "name") String bucketName) {
        storageService.createBucket(bucketName);
    }

    @Operation(summary = "Get all the buckets in S3")
    @GetMapping
    public List<String> getAllBuckets() {
        return storageService.getAllBuckets();
    }

    @Operation(summary = "Delete the bucket identified by give name")
    @DeleteMapping
    public void deleteBucket(@RequestParam(value = "name") String bucketName) {
        storageService.deleteBucket(bucketName);
    }

    // List Objects
    @Operation(summary = "Return all the files in the bucket identified by name")
    @GetMapping("/{bucket-name}")
    public List<String> getAllObjectsFromBucket(@PathVariable("bucket-name") String bucketName) {
        return storageService.getAllObjectsFromBucket(bucketName);
    }

    @Operation(summary = "Create folder in an s3 bucket")
    @PostMapping("/folder")
    public void createFolder(@RequestParam(value = "bucket") String bucket,
                             @RequestParam(value = "folder") String folder) {
        storageService.createFolder(bucket, folder);
    }


    @Operation(summary = "Add life cycle configuration to given bucket")
    @PutMapping("/lifecycle")
    public void createLifeCycleConfig(@RequestParam(value = "bucketName") String bucket,
                                      @RequestParam(value = "prefix") String prefix,
                                      @RequestParam(value = "days") Integer days) {
        storageService.createLifecyclePolicy(bucket, prefix, days);
    }
}
