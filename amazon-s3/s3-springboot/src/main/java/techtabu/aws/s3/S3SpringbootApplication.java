package techtabu.aws.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class S3SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3SpringbootApplication.class, args);
    }

}
