package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureCommandService;
import com.poorlex.poorlex.support.ControllerTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("지출 관리 Controller 단위 테스트")
@WebMvcTest(
        controllers = ExpenditureCommandController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}
)
class ExpenditureCommandControllerTest extends ControllerTest implements ReplaceUnderScoreTest {

    @MockBean
    private ExpenditureCommandService expenditureCommandService;

    @BeforeEach
    void setUp() {
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
    }

    @Test
    void 지출을_생성한다_메인_이미지만_있을_때() throws Exception {
        //given
        final MockMultipartFile 지출_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_메인_이미지)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 지출을_생성한다_메인이미지와_서브이미지가_있을_때() throws Exception {
        //given
        final MockMultipartFile 지출_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");
        final MockMultipartFile 지출_서브_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("subImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_메인_이미지)
                                .file(지출_서브_이미지)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @ParameterizedTest(name = "지출이 {0} 일 때")
    @ValueSource(strings = {"-1", "10000000"})
    void ERROR_지출생성시_금액이_적절하지_않을_경우_400_상태코드로_응답한다(final String amount) throws Exception {
        //given
        STUBBING_요청을_지출로_변환하는_실제_로직이_실행된다();
        final MockMultipartFile 지출_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_메인_이미지)
                                .queryParam("amount", amount)
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_설명이_비어있는_경우_400_상태코드로_응답한다() throws Exception {
        //given
        STUBBING_요청을_지출로_변환하는_실제_로직이_실행된다();
        final MockMultipartFile 지출_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_메인_이미지)
                                .queryParam("amount", "1000")
                                .queryParam("description", "  ")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_설명이_30자를_넘는_경우_400_상태코드로_응답한다() throws Exception {
        //given
        STUBBING_요청을_지출로_변환하는_실제_로직이_실행된다();
        final MockMultipartFile 지출_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_메인_이미지)
                                .queryParam("amount", "1000")
                                .queryParam("description", "a".repeat(31))
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void ERROR_지출생성시_메인이미지가_없는_경우_400_상태코드로_응답한다() throws Exception {
        //given
        STUBBING_요청을_지출로_변환하는_실제_로직이_실행된다();
        final MockMultipartFile 지출_서브_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("subImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .file(지출_서브_이미지)
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required part 'mainImage' is not present."));
    }

    @Test
    void ERROR_지출생성시_이미지가_없는_경우_400_상태코드로_응답한다() throws Exception {
        //given when then
        mockMvc.perform(
                        multipart(HttpMethod.POST, "/expenditures")
                                .queryParam("amount", "1000")
                                .queryParam("description", "소개")
                                .queryParam("date", LocalDate.now().toString())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required part 'mainImage' is not present."));
    }

    @Test
    void 지출을_수정한다() throws Exception {
        //given
        final Long 수정하려는_지출_ID = 1L;
        final MockMultipartFile 변경할_메인_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("mainImage");
        final MockMultipartFile 변경할_서브_이미지 = 해당_키를_가지는_MultipartFile을_생성한다("subImage");

        //when
        //then
        mockMvc.perform(
                        multipart(HttpMethod.PUT, "/expenditures/" + 수정하려는_지출_ID)
                                .file(변경할_메인_이미지)
                                .file(변경할_서브_이미지)
                                .queryParam("amount", "2000")
                                .queryParam("description", "업데이트된 소개")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private MockMultipartFile 해당_키를_가지는_MultipartFile을_생성한다(final String key) throws IOException {
        return new MockMultipartFile(
                key,
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );
    }

    private void STUBBING_요청을_지출로_변환하는_실제_로직이_실행된다() {
        when(expenditureCommandService.createExpenditure(any(), any(), any(), any())).thenCallRealMethod();
    }
}
