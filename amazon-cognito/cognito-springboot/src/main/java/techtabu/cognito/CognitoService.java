package techtabu.cognito;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TechTabu
 */

@Service
@Slf4j
public class CognitoService {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    private CognitoIdentityClient cognitoClient;
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @PostConstruct
    public void createClient() {
        cognitoClient = CognitoIdentityClient.create();
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.create();
    }

    public void createNewUser(String username, String email, String password) {
        AttributeType attributeType = AttributeType.builder()
                .name("email")
                .value(email)
                .build();

        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .temporaryPassword(password)
                .userAttributes(attributeType)
                .messageAction("SUPPRESS")
                .build();

        AdminCreateUserResponse response = cognitoIdentityProviderClient.adminCreateUser(request);

        log.info("Created user: {}", response.user());
    }

    public void setNewPassword(String username, String newPassword) {
        log.info("setting new password for user: {}", username);
        AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .password(newPassword)
                .permanent(true)
                .build();

        AdminSetUserPasswordResponse response = cognitoIdentityProviderClient.adminSetUserPassword(request);

        log.info("Password set for username: {}, with response: {}", username, response.toString());
    }

    public List<String> getAllUsers() {
        ListUsersRequest request = ListUsersRequest.builder()
                .userPoolId(userPoolId)
                .build();

        List<String> users = new ArrayList<>();

        ListUsersResponse response = cognitoIdentityProviderClient.listUsers(request);

        response.users().forEach(u -> {
            log.info("User username: {}, status: {}", u.username(), u.userStatus());
            users.add(u.username());
        });

        return users;
    }
}
