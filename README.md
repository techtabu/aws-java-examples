
Project Name: Daedalus code: DDLS


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

These examples are set to use SDK 2.0 version. 
Please follow the [migration guide](https://docs.aws.amazon.com/sdk-for-java/v2/migration-guide/what-is-java-migration.html)
for if you have any questions regarding the changes. 

Also, you can checkout the [SDK Developer guide](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html)
as well. 

To use SDK it would be easier and recommended to use SDK bom. Add the following to your pom file,

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
          <groupId>software.amazon.awssdk</groupId>
          <artifactId>bom</artifactId>
          <version>2.x.0</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

And then, declare the required module under dependencies,
```xml
<dependencies>
    <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>s3</artifactId>
    </dependency>
</dependencies>
```