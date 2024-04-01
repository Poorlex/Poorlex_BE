package com.poorlex.poorlex.consumption.expenditure.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExpenditureImageService {

    String saveAndReturnPath(MultipartFile image, String bucketDirectory);

    void delete(String mainImageUrl);
}
