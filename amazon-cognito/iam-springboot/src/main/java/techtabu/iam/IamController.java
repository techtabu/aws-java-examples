package techtabu.iam;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TechTabu
 */

@RestController
@RequestMapping("/iam")
@Slf4j
public class IamController {

    private final StsService stsService;

    public IamController(StsService stsService) {
        this.stsService = stsService;
    }

    @Operation(summary = "Get temporary credential to access S3 using cognito auth")
    @GetMapping("/sts/temp-token")
    public CredentialDTO getTemporaryToken(String identityToken) {
        return stsService.getTempCredentials(identityToken);
    }
}
