package techtabu.aws.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sqs-springboot")
@Slf4j
public class SQSSpringBootController {

    SqsClient amazonSQS;

    @PostConstruct
    public void createClient() {
        amazonSQS = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @PostMapping("/queue")
    public String createStandardQueue(@RequestParam("queueName") String queueName, @RequestParam("isFifo") boolean isFifo) {
        log.info("Creating Queue with name: {} & isFifo: {}", queueName, isFifo);

        final CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();

        if (isFifo) {
            final Map<String, String> attributes = new HashMap<>();

            // A FIFO queue must have the FifoQueue attribute set to True
            attributes.put("FifoQueue", "true");

            // If the user doesn't provide a MessageDeduplicationId, generate a MessageDeduplicationId based on the content.
            attributes.put("ContentBasedDeduplication", "true");
            createQueueRequest.attributesAsStrings().putAll(attributes);
        }


        final String myQueueUrl = amazonSQS.createQueue(createQueueRequest).queueUrl();

        log.info("Created queue {}, with URL: {}", queueName, myQueueUrl);
        return myQueueUrl;
    }

    @GetMapping("/queues")
    public List<String> getAllQueues(@RequestParam("prefix") String prefix) {
        log.info("Getting all queues with {}", prefix);

        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
        List<String> queueURLs = amazonSQS.listQueues(listQueuesRequest).queueUrls();

        queueURLs.forEach(q -> log.info("Queue URL: {}", q));
        return queueURLs;
    }

    @GetMapping("/queue/{name}")
    public String getQueueUrl(@PathVariable("name") String queueName) {
        GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
        String queueUrl = amazonSQS.getQueueUrl(request).queueUrl();

        log.info("queue url for: {} is {}", queueName, queueUrl);

        return queueUrl;
    }

    @DeleteMapping("/queue/delete")
    public void deleteQueue(@RequestParam("queueName") String queueName) {
        log.info("Deleting Queue: {}", queueName);

        try {
            GetQueueUrlRequest request = GetQueueUrlRequest.builder().queueName(queueName).build();
            String queueUrl = amazonSQS.getQueueUrl(request).queueUrl();
            DeleteQueueRequest deleteRequest = DeleteQueueRequest.builder().queueUrl(queueUrl).build();
            amazonSQS.deleteQueue(deleteRequest);
        } catch (SqsException e) {
            log.error("exception in deleting queue {}", e);
        }

    }

    @PostMapping("/message/")
    public void sendFifoMessage(@RequestBody SampleMessage sampleMessage) {

        String messageGroupId = null;

        if (sampleMessage.isFifo()) {
            messageGroupId = sampleMessage.getMessageGroup();
        }

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(sampleMessage.getQueueURL())
                .messageGroupId(messageGroupId)
                .messageBody(sampleMessage.getMessage())
                .build();


        final SendMessageResponse sendMessageResponse = amazonSQS.sendMessage(sendMessageRequest);
        log.info("Message sent successfully with message Id: {} and sequence number: {}", sendMessageResponse.messageId(), sendMessageResponse.sequenceNumber());
    }

    @GetMapping("/messages")
    public List<String> receiveAndDeleteMessages(@RequestParam("queueURL") String queueURL) {
        log.info("Receiving messages from: {}", queueURL);
        final ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueURL)
                .build();
        final List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).messages();
        messages.forEach(m -> {
            log.info("  MessageId:     " + m.messageId());
            log.info("  ReceiptHandle: " + m.receiptHandle());
            log.info("  MD5OfBody:     " + m.md5OfBody());
            log.info("  Body:          " + m.body());
            for (final Map.Entry<String, String> entry : m.attributesAsStrings().entrySet()) {
                log.info("Attribute");
                log.info("  Name:  " + entry.getKey());
                log.info("  Value: " + entry.getValue());
            }

            log.info("Deleting message");
            final String receiptHandle = m.receiptHandle();
            amazonSQS.deleteMessage(DeleteMessageRequest.builder().queueUrl(queueURL).receiptHandle(receiptHandle).build());
        });

        List<String> messageBodies = messages.stream().map(Message::body)
                .collect(Collectors.toList());

        return messageBodies;

    }


    @PostMapping("/simulate")
    public void simulateAll() {

        log.info("===============================================");
        log.info("Getting Started with Amazon SQS Standard Queues");
        log.info("===============================================\n");

        try {

            // Create a queue.
            log.info("Creating a new SQS queue called MyQueue.\n");
            final CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName("MyQueue").build();
            final String myQueueUrl = amazonSQS.createQueue(createQueueRequest).queueUrl();

            // List all queues.
            log.info("Listing all queues in your account.\n");
            for (final String queueUrl : amazonSQS.listQueues().queueUrls()) {
                log.info("  QueueUrl: " + queueUrl);
            }
            log.info("\n ******** \n");

            // Send a message.
            log.info("Sending a message to MyQueue.\n");
            amazonSQS.sendMessage(SendMessageRequest.builder()
                    .queueUrl(myQueueUrl)
                    .messageBody("This is my message text.")
                    .build());

            // Receive messages.
            log.info("Receiving messages from MyQueue.\n");
            final ReceiveMessageRequest receiveMessageRequest =  ReceiveMessageRequest.builder()
                    .queueUrl(myQueueUrl)
                    .build();
            final List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).messages();

            for (final Message message : messages) {
                log.info("Message");
                log.info("  MessageId:     " + message.messageId());
                log.info("  ReceiptHandle: " + message.receiptHandle());
                log.info("  MD5OfBody:     " + message.md5OfBody());
                log.info("  Body:          " + message.body());
                for (final Map.Entry<String, String> entry : message.attributesAsStrings().entrySet()) {
                    log.info("Attribute");
                    log.info("  Name:  " + entry.getKey());
                    log.info("  Value: " + entry.getValue());
                }
            }
            log.info("\n ********* \n");

            // Delete the message.
            log.info("Deleting a message.\n");
            final String messageReceiptHandle = messages.get(0).receiptHandle();
            amazonSQS.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(myQueueUrl)
                    .receiptHandle(messageReceiptHandle)
                    .build());

            // Delete the queue.
            log.info("Deleting the test queue.\n");
            amazonSQS.deleteQueue(DeleteQueueRequest.builder().queueUrl(myQueueUrl).build());

        }  catch (SqsException ase) {
            log.info("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            log.info("Error Message:    " + ase.getMessage());
            log.info("HTTP Status Code: " + ase.statusCode());
            log.info("AWS Error Code:   " + ase.awsErrorDetails().errorCode());

        }
    }
}
