package techtabu.cognito;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author TechTabu
 */

@RestController
@RequestMapping("/cognito")
@Slf4j
public class CognitoController {

    private final CognitoService cognitoService;

    public CognitoController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public void createNewUser(@RequestParam("username") String username,
                              @RequestParam("email") String email,
                              @RequestParam("password") String password) {
        cognitoService.createNewUser(username, email, password);
    }

    @Operation(summary = "Set new permanent password for user")
    @PostMapping("/new-password")
    public void setNewPassword(@RequestParam("username") String username,
                               @RequestParam("newPassword") String newPassword) {
        cognitoService.setNewPassword(username, newPassword);
    }

    @Operation(summary = "get all users - only username")
    @GetMapping
    public List<String> getAllUsers() {
        return cognitoService.getAllUsers();
    }
}
