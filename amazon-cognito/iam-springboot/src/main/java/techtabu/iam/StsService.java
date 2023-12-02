package techtabu.iam;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;

import java.time.Instant;

/**
 * @author TechTabu
 */

@Slf4j
@Service
public class StsService {

    @Value("${aws.iamPrefix}")
    private String iamPrefix;

    @Value("${aws.assumedRoleName}")
    private String assumedRoleName;

    @Value("${aws.sessionDurationSeconds}")
    private Integer sessionDurationSeconds;

    @Value("${aws.stsAdminAccessKey}")
    private String stsAdminAccessKey;

    @Value("${aws.stsAdminSecretKey}")
    private String stsAdminSecretKey;


    private StsClient stsClient;

    @PostConstruct
    public void createIamClient() {
        log.info("Values for iamPrefix: {}, role name: {}", iamPrefix, assumedRoleName);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(stsAdminAccessKey, stsAdminSecretKey);
        stsClient = StsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public CredentialDTO getTempCredentials(String identityToken) {
        String roleSessionName = assumedRoleName + Instant.now().toEpochMilli();
        String roleArn = iamPrefix + ":role/" + assumedRoleName;
        log.info("Creating request with session name: {} for role arn: {}", roleSessionName, roleArn);
        AssumeRoleWithWebIdentityRequest request = AssumeRoleWithWebIdentityRequest.builder()
                .roleSessionName(roleSessionName)
                .roleArn(roleArn)
                .webIdentityToken(identityToken)
                .durationSeconds(sessionDurationSeconds)
                .build();
        log.info("Caller name: {}", stsClient.getCallerIdentity());
        AssumeRoleWithWebIdentityResponse response = stsClient.assumeRoleWithWebIdentity(request);

        return CredentialDTO.builder()
                .accessKeyId(response.credentials().accessKeyId())
                .secretAccessKey(response.credentials().secretAccessKey())
                .sessionToken(response.credentials().sessionToken())
                .expiryTimeEpochMillis(response.credentials().expiration().toEpochMilli())
                .build();
    }
}
