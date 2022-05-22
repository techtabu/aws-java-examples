package techtabu.aws.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


/**
 * @author TechTabu
 */
@Data
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private String id;
    private String name;
    private String email;
    private Long regDate;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "Id")
    public String getId() {
        return this.id;
    }

    @DynamoDbAttribute(value = "Name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute(value = "Email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute(value = "RegDate")
    public Long getRegDate() {
        return regDate;
    }
}
