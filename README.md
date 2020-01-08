To use these applications, AWS credential should be saved in `~/.aws/credentials` file as below,
```properties
[default]
aws_access_key_id = your_access_key_id
aws_secret_access_key = your_secret_access_key
```
In addition, your AWS region should be configured in `~/.aws/config` file. 
```properties
[default]
region = your_aws_region
```
You can follow [aws tutorial](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html) 
for setting up region and credentials.

