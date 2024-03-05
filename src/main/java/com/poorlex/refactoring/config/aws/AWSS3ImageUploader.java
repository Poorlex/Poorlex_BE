package com.poorlex.refactoring.config.aws;

import com.poorlex.refactoring.expenditure.service.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
public class AWSS3ImageUploader implements ImageUploader {

    private final AwsUtil awsUtil;

    @Override
    public String uploadAndReturnPath(final MultipartFile file, final String path) {
        return awsUtil.uploadS3File(file, path);
    }
}
