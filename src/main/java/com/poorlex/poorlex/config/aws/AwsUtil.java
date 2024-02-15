package com.poorlex.poorlex.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class AwsUtil {

    private static final Regions SEOUL_REGION = Regions.AP_NORTHEAST_2;

    private final String accessKey;

    private final String secretKey;

    private final String bucket;

    public AwsUtil(@Value("${aws.s3.accesskey}") final String accessKey,
                   @Value("${aws.s3.secretKey}") final String secretKey,
                   @Value("${aws.s3.bucket}") final String bucket) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
    }

    private AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    private AmazonS3 awsS3Client() {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
            .withRegion(SEOUL_REGION)
            .build();
    }

    public String uploadS3File(final MultipartFile file, final String directory) {
        final AmazonS3 amazonS3 = awsS3Client();
        final String fileName = file.getOriginalFilename();

        PutObjectResult result;
        try (final InputStream inputStream = file.getInputStream()) {
            result = amazonS3.putObject(bucket, directory + "/" + fileName, inputStream,
                createFileObjectMetadata(file));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        log.info("upload resut : {}", result);
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private ObjectMetadata createFileObjectMetadata(final MultipartFile file) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        return objectMetadata;
    }

    public S3ObjectInputStream downloadS3File(final String fileName) {
        final S3Object s3Object = awsS3Client().getObject(bucket, fileName);
        return s3Object.getObjectContent();
    }
}
