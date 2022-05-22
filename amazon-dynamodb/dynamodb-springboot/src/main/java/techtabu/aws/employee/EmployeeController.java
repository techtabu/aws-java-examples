package techtabu.aws.employee;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TechTabu
 */

@RestController
@RequestMapping("/dynamodb/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{loginAlias}")
    public Employee getEmployeeByAlias(@PathVariable("loginAlias") String loginAlias) {
        return employeeService.getEmployeeByLoginAlias(loginAlias);
    }

    @PutMapping("/{loginAlias}")
    public Employee updateFirstName(@PathVariable("loginAlias") String loginAlias,
                                    @RequestParam("firstName") String firstName) {
        return employeeService.updateFirstName(loginAlias, firstName);
    }
}
