package com.poorlex.refactoring.config.spring;

import com.poorlex.refactoring.config.aws.AWSS3ImageUploader;
import com.poorlex.refactoring.config.aws.AwsConfiguration;
import com.poorlex.refactoring.config.aws.AwsUtil;
import com.poorlex.refactoring.expenditure.service.ImageUploader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    private final AwsConfiguration awsConfiguration;

    public SpringConfiguration(final AwsConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    @Bean
    public ImageUploader imageUploader() {
        return new AWSS3ImageUploader(awsUtil());
    }

    @Bean
    public AwsUtil awsUtil() {
        return new AwsUtil(
            awsConfiguration.getRegion(),
            awsConfiguration.getAccessKey(),
            awsConfiguration.getSecretKey(),
            awsConfiguration.getBucket()
        );
    }
}
