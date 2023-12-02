package techtabu.iam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author TechTabu
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialDTO {

    private String accessKeyId;
    private String secretAccessKey;

    /**
     * AWS session token associated the temporary credentials.
     * When using temporary credentials the sessionToken should be included in the request
     * using {@code AwsSessionCredentials}
     */
    private String sessionToken;
    private long expiryTimeEpochMillis;
}
