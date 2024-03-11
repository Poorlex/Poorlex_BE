package com.poorlex.poorlex.config.aws;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AWSS3Service {

    private final AwsUtil awsUtil;

    public String uploadMultipartFile(final MultipartFile file, final String directory) {
        return awsUtil.uploadS3File(file, directory);
    }

    public byte[] downloadFile(final String fileName) {
        final S3ObjectInputStream s3ObjectInputStream = awsUtil.downloadS3File(fileName);
        try {
            return s3ObjectInputStream.readAllBytes();
        } catch (IOException e) {
            log.warn("파일 다운에 실패하였습니다. ( 파일명 : {} )", fileName);
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void deleteFile(final String fileUri) {
        awsUtil.deleteS3File(fileUri);
    }
}
