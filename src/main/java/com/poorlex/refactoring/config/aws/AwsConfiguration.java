package com.poorlex.refactoring.config.aws;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsConfiguration {

    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;

    public void setRegion(final String region) {
        if (Objects.isNull(this.region)) {
            this.region = region;
        }
    }

    public void setAccessKey(final String accessKey) {
        if (Objects.isNull(this.accessKey)) {
            this.accessKey = accessKey;
        }
    }

    public void setSecretKey(final String secretKey) {
        if (Objects.isNull(this.secretKey)) {
            this.secretKey = secretKey;
        }
    }

    public void setBucket(final String bucket) {
        if (Objects.isNull(this.bucket)) {
            this.bucket = bucket;
        }
    }

    public String getRegion() {
        return region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucket() {
        return bucket;
    }
}
