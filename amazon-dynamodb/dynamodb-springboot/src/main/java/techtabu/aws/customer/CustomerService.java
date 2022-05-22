package techtabu.aws.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import techtabu.aws.config.EnhancedDBConfig;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author TechTabu
 */

@Service
@Slf4j
public class CustomerService {

    private final EnhancedDBConfig enhancedDBConfig;

    public CustomerService(EnhancedDBConfig enhancedDBConfig) {
        this.enhancedDBConfig = enhancedDBConfig;
    }

    private DynamoDbTable<Customer> customerTable() {
        return enhancedDBConfig.getEnhancedClient()
                .table("customers", TableSchema.fromBean(Customer.class));
    }

    public void createCustomerTable() {
        log.info("creating customer table");
        customerTable().createTable();
    }

    public void deleteCustomerTable() {
        log.info("Deleting customer table");
        customerTable().deleteTable();
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerTable().scan().items().stream().collect(Collectors.toList());
        log.info("Received {} customers", customers.size());

        return customers;
    }

    public Customer createCustomer(Customer customer) {

        if (!StringUtils.hasText(customer.getId())) {
            customer.setId(UUID.randomUUID().toString());
        }

        if (customer.getRegDate() == null) {
            customer.setRegDate(Instant.now().getEpochSecond());
        }

        log.info("creating random customer: {}", customer);
        customerTable().putItem(customer);

        return customer;

    }

    public Customer createRandomCustomer() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID().toString())
                .name("Tabu")
                .email("tabu@gmail.com")
                .regDate(Instant.now().getEpochSecond())
                .build();

        log.info("creating random customer: {}", customer);
        customerTable().putItem(customer);

        return customer;

    }
}
