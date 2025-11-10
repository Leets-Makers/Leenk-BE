package leets.leenk.global.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.global.auth.application.dto.request.RefreshTokenRequest;
import leets.leenk.global.auth.application.dto.request.UsernamePasswordLoginRequest;
import leets.leenk.global.auth.application.dto.response.LoginResponse;
import leets.leenk.global.auth.application.usecase.AuthUsecase;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthUsecase authUsecase;

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 login api [for mobile]")
    public CommonResponse<LoginResponse> kakaoLogin(@RequestHeader("Kakao-Access-Token") String kakaoAccessToken) {
        LoginResponse response = authUsecase.kakaoLogin(kakaoAccessToken);

        if (response.userId() == null) {
            return CommonResponse.success(ResponseCode.LOGIN_SUCCESS, response);
        }

        return CommonResponse.success(ResponseCode.INITIAL_LOGIN_SUCCESS, response);
    }

    @PostMapping("/apple/login")
    @Operation(summary = "애플 login api [for mobile]")
    public CommonResponse<LoginResponse> appleLogin(@RequestHeader("Apple-Identity-Token") String appleIdToken) {
        LoginResponse response = authUsecase.appleLogin(appleIdToken);

        if (response.userId() == null) {
            return CommonResponse.success(ResponseCode.LOGIN_SUCCESS, response);
        }

        return CommonResponse.success(ResponseCode.INITIAL_LOGIN_SUCCESS, response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급 API")
    public CommonResponse<LoginResponse> reissueToken(@RequestBody @Valid RefreshTokenRequest request) {
        LoginResponse response = authUsecase.reissueToken(request);

        return CommonResponse.success(ResponseCode.REFRESH_TOKEN_SUCCESS, response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public CommonResponse<LoginResponse> login(@Valid @RequestBody UsernamePasswordLoginRequest request) {
        LoginResponse response = authUsecase.login(request);

        return CommonResponse.success(ResponseCode.LOGIN_SUCCESS, response);
    }
}
