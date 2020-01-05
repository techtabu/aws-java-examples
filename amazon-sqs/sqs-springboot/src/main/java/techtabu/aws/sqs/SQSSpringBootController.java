package techtabu.aws.sqs;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sqs-springboot")
@Slf4j
public class SQSSpringBootController {

    AmazonSQS amazonSQS;

    @PostConstruct
    public void createClient() {
        amazonSQS = AmazonSQSClientBuilder.defaultClient();
    }

    @PostMapping("/create")
    public String createQueue(@RequestBody String queueName) {
        log.info("Creating Queue with name: {}", queueName);

        final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        final String myQueueUrl = amazonSQS.createQueue(createQueueRequest).getQueueUrl();

        log.info("Created queue {}, with URL: {}", queueName, myQueueUrl);
        return myQueueUrl;
    }

    @GetMapping("/getall")
    public List<String> getAllQueues() {
        log.info("Getting all queues");
        List<String> queueURLs = amazonSQS.listQueues().getQueueUrls();
        queueURLs.forEach(q -> log.info("Queue URL: {}", q));
        return queueURLs;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody SampleMessage sampleMessage) {
        log.info("Sending \n\t message {} \n\tto queue: {}.", sampleMessage.getMessage(), sampleMessage.getQueueURL());
        amazonSQS.sendMessage(new SendMessageRequest(sampleMessage.getQueueURL(), sampleMessage.getMessage()));
    }

    @GetMapping("/receive")
    public List<String> receiveAndDeleteMessages(@RequestParam("queueURL") String queueURL) {
        log.info("Receiving messages from: {}", queueURL);
        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
        final List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        messages.forEach(m -> {
            log.info("  MessageId:     " + m.getMessageId());
            log.info("  ReceiptHandle: " + m.getReceiptHandle());
            log.info("  MD5OfBody:     " + m.getMD5OfBody());
            log.info("  Body:          " + m.getBody());
            for (final Map.Entry<String, String> entry : m.getAttributes().entrySet()) {
                log.info("Attribute");
                log.info("  Name:  " + entry.getKey());
                log.info("  Value: " + entry.getValue());
            }

            log.info("Deleting message");
            final String receiptHandle = m.getReceiptHandle();
            amazonSQS.deleteMessage(new DeleteMessageRequest(queueURL, receiptHandle));
        });

        List<String> messageBodies = messages.stream().map(Message::getBody)
                .collect(Collectors.toList());

        return messageBodies;

    }

    @DeleteMapping("/delete")
    public void deleteQueue(@RequestParam("queueURL") String queueURL) {
        log.info("Deleting Queue: {}", queueURL);
        amazonSQS.deleteQueue(new DeleteQueueRequest(queueURL));
    }


    @PostMapping("/simulate")
    public void simulateAll() {

        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        log.info("===============================================");
        log.info("Getting Started with Amazon SQS Standard Queues");
        log.info("===============================================\n");

        try {

            // Create a queue.
            log.info("Creating a new SQS queue called MyQueue.\n");
            final CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
            final String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

            // List all queues.
            log.info("Listing all queues in your account.\n");
            for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
                log.info("  QueueUrl: " + queueUrl);
            }
            log.info("\n ******** \n");

            // Send a message.
            log.info("Sending a message to MyQueue.\n");
            sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));

            // Receive messages.
            log.info("Receiving messages from MyQueue.\n");
            final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

            for (final Message message : messages) {
                log.info("Message");
                log.info("  MessageId:     " + message.getMessageId());
                log.info("  ReceiptHandle: " + message.getReceiptHandle());
                log.info("  MD5OfBody:     " + message.getMD5OfBody());
                log.info("  Body:          " + message.getBody());
                for (final Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                    log.info("Attribute");
                    log.info("  Name:  " + entry.getKey());
                    log.info("  Value: " + entry.getValue());
                }
            }
            log.info("\n ********* \n");

            // Delete the message.
            log.info("Deleting a message.\n");
            final String messageReceiptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));

            // Delete the queue.
            log.info("Deleting the test queue.\n");
            sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));

        }  catch (final AmazonServiceException ase) {
            log.info("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            log.info("Error Message:    " + ase.getMessage());
            log.info("HTTP Status Code: " + ase.getStatusCode());
            log.info("AWS Error Code:   " + ase.getErrorCode());
            log.info("Error Type:       " + ase.getErrorType());
            log.info("Request ID:       " + ase.getRequestId());

        } catch (final AmazonClientException ace) {
            log.info("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            log.info("Error Message: " + ace.getMessage());
        }
    }
}
