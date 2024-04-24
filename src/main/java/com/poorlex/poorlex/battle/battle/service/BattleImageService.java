package com.poorlex.poorlex.battle.battle.service;

import org.springframework.web.multipart.MultipartFile;

public interface BattleImageService {

    String saveAndReturnPath(MultipartFile image, String bucketDirectory);

    void delete(String mainImageUrl);
}
