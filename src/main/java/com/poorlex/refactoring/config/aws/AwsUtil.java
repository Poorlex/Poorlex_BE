package com.poorlex.refactoring.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class AwsUtil {

    private final Regions region;

    private final String accessKey;

    private final String secretKey;

    private final String bucket;

    public AwsUtil(final String region, final String accessKey, final String secretKey, final String bucket) {
        this.region = Regions.fromName(region);
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
    }

    public String uploadS3File(final MultipartFile file, final String directory) {
        final AmazonS3 amazonS3 = awsS3Client();
        final String fileName = file.getOriginalFilename();

        try (final InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(bucket, directory + "/" + fileName, inputStream, createFileObjectMetadata(file));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private AmazonS3 awsS3Client() {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
            .withRegion(region)
            .build();
    }

    private AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    private ObjectMetadata createFileObjectMetadata(final MultipartFile file) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        return objectMetadata;
    }
}
