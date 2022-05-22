package techtabu.aws.employee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import techtabu.aws.config.EnhancedDBConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TechTabu
 */

@Service
@Slf4j
public class EmployeeService {

    private final EnhancedDBConfig enhancedDBConfig;

    public EmployeeService(EnhancedDBConfig enhancedDBConfig) {
        this.enhancedDBConfig = enhancedDBConfig;
    }

    private DynamoDbTable<Employee> employeeTable() {
        return enhancedDBConfig.getEnhancedClient()
                .table("Employee", TableSchema.fromBean(Employee.class));
    }

    public List<Employee> getAllEmployees() {
//        DynamoDbTable<Employee> employeeTable =
        List<Employee> employees = employeeTable().scan().items().stream().collect(Collectors.toList());
        log.info("received {} items from table", employees.size());
        return employees;

    }

    public Employee getEmployeeByLoginAlias(String loginAlias) {

        Key key = Key.builder().partitionValue(loginAlias).build();
        Employee employee = employeeTable().getItem(r -> r.key(key));

        log.info("retrieved employee: {} by alias", employee);
        return employee;
    }

    public Employee updateFirstName(String loginAlias, String firstName) {
        Employee employee = getEmployeeByLoginAlias(loginAlias);

        if (employee == null) {
            log.error("could not find employee for given loginAlias: {}", loginAlias);
            return null;
        }
        employee.setFirstName(firstName);
        employeeTable().updateItem(employee);
        return employee;
    }

//    public List<Employee> searchRecordsForFirstName(String queryString) {
//        QueryConditional queryConditional = QueryConditional.sortBeginsWith(Key.builder().build());
//        QuerySp
//    }

}
