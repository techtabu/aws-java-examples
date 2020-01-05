package techtabu.aws.sqs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleMessage {

    private String queueURL;
    private String message;
}
