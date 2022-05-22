package techtabu.aws.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Set;

/**
 * @author TechTabu
 */

@Data
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String firstName;
    private String lastName;
    private String loginAlias;
    private String managerLoginAlias;
    private String designation;
    private Set<String> skills;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "LoginAlias")
    public String getLoginAlias() {
        return this.loginAlias;
    }

    @DynamoDbAttribute(value = "FirstName")
    public String getFirstName() {
        return this.firstName;
    }

    @DynamoDbAttribute(value = "LastName")
    public String getLastName() {
        return this.lastName;
    }

    @DynamoDbAttribute(value = "ManagerLoginAlias")
    public String getManagerLoginAlias() {
        return this.managerLoginAlias;
    }

    @DynamoDbAttribute(value = "Designation")
    public String getDesignation() {
        return this.designation;
    }

    @DynamoDbAttribute(value = "Skills")
    public Set<String> getSkills() {
        return this.skills;
    }
}
