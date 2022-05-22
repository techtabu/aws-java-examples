package techtabu.aws.sqs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleMessage {

    private String queueURL;
    private List<String> messages;
    private String messageGroup;
    private boolean fifo;
}
