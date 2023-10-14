package techtabu.aws.sns.springboot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author TechTabu
 */

@Slf4j
@RestController
@RequestMapping("/sns-springboot")
public class SNSController {

    SnsClient snsClient;

    @PostConstruct
    public void createSNSClient() {
        snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @PostMapping("/topic")
    public String createNewTopic(@RequestParam("topicName") String topicName) {

        CreateTopicResponse response;

        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            response = snsClient.createTopic(request);
            return response.topicArn();
        } catch (SnsException e) {
            log.error("Error creating SNS topic", e);
            throw e;
        }
    }

    @GetMapping("/topics")
    public List<String> getAllTopics() {
        try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                    .build();
            ListTopicsResponse response = snsClient.listTopics(request);
            return response.topics().stream()
                    .map(Topic::topicArn).collect(Collectors.toList());
        } catch (SnsException e) {
            throw e;
        }
    }

    @GetMapping("/subscriptions")
    public Map<String, List<String>> getAllSubscriptions() {
        try {
            ListSubscriptionsRequest request = ListSubscriptionsRequest.builder()
                    .build();

            ListSubscriptionsResponse response = snsClient.listSubscriptions(request);
            return response.subscriptions().stream()
                    .collect(Collectors.groupingBy(Subscription::topicArn,
                            HashMap::new,
                            Collectors.mapping(Subscription::subscriptionArn, Collectors.toList())));
        } catch (SnsException e) {
            throw e;
        }
    }

    @PostMapping("/publish")
    public void publishMessage(@RequestParam("topicArn") String topicArn,
                               @RequestParam("message") String message) {

        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();
            snsClient.publish(request);
        } catch (SnsException e) {
            throw  e;
        }
    }


}
