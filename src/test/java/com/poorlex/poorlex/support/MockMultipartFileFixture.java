package com.poorlex.poorlex.support;

import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MockMultipartFileFixture {

    public static MockMultipartFile get() throws IOException {
        return new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
    }
}
