package com.poorlex.poorlex.auth.documentation;

import com.poorlex.poorlex.auth.controller.AuthController;
import com.poorlex.poorlex.auth.service.AuthService;
import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.support.security.DocumentTestMockMvcConfigure;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DocumentTestMockMvcConfigure
@WebMvcTest(AuthController.class)
class AuthDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void login_or_register() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("oauthId", "nickname");
        given(authService.loginAfterRegisterIfNotExist(any())).willReturn(new LoginTokenResponse("accessToken"));

        //when
        final ResultActions result = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("login-or-register",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰")
                                 )
                        ));
    }
}
