package techtabu.aws.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TechTabu
 */
@RestController
@RequestMapping("/dynamodb/customers")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/table")
    public void createCustomerTable() {
        customerService.createCustomerTable();
    }

    @DeleteMapping("table")
    public void deleteCustomerTable() {
        customerService.deleteCustomerTable();
    }

    @PostMapping("/random")
    public Customer createRandomCustomer() {
        return customerService.createRandomCustomer();
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
}
