package com.poorlex.poorlex.external.aws;

import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AWSS3ExpenditureImageService implements ExpenditureImageService {

    private final AwsUtil awsUtil;

    @Override
    public String saveAndReturnPath(final MultipartFile file, final String directory) {
        return awsUtil.uploadS3File(file, directory);
    }

    @Override
    public void delete(final String filePath) {
        awsUtil.deleteS3File(filePath);
    }
}
