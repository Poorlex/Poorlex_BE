package com.poorlex.refactoring.expenditure.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {

    String uploadAndReturnPath(final MultipartFile file, final String path);
}
