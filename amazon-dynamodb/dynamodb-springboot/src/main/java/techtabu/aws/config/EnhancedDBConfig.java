package techtabu.aws.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * @author TechTabu
 */

@Configuration
@Slf4j
public class EnhancedDBConfig {

    @Value("${dynamodb.local: true}")
    private boolean isLocal = true;

    public DynamoDbClient getClient() {

        if (isLocal) {
            log.info("creating local client");
            return DynamoDbClient.builder()
                    .endpointOverride(URI.create("http://localhost:8000"))
                    // The region is meaningless for local DynamoDb but required for client builder validation
                    .region(Region.US_EAST_1)
//                    .credentialsProvider(StaticCredentialsProvider.create(
//                            AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                    .build();
        } else {
            log.info("creating client in US-EAST");
            Region region = Region.US_EAST_1;
            DynamoDbClient ddb = DynamoDbClient.builder()
                    .region(region)
                    .build();
            return ddb;
        }

    }

    public DynamoDbEnhancedClient getEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getClient())
                .build();
    }
}
