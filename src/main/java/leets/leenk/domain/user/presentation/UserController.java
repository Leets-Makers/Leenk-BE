package leets.leenk.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.user.application.dto.request.*;
import leets.leenk.domain.user.application.dto.response.UserInfoResponse;
import leets.leenk.domain.user.application.usecase.UserUsecase;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static leets.leenk.domain.user.presentation.ResponseCode.*;

@Tag(name = "USER")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUsecase userUsecase;

    @PatchMapping("/agreement")
    @Operation(summary = "약관 동의 입력")
    public CommonResponse<Void> initialAgreement(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                 @RequestBody @Valid AgreementRequest request) {
        userUsecase.initialAgreement(userId, request);

        return CommonResponse.success(UPDATE_AGREEMENT);
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "기본 정보 입력")
    public CommonResponse<Void> completeProfile(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                @RequestBody @Valid RegisterRequest request) {
        userUsecase.completeProfile(userId, request);

        return CommonResponse.success(COMPLETE_PROFILE);
    }

    @GetMapping("/me")
    @Operation(summary = "마이페이지 조회 API")
    public CommonResponse<UserInfoResponse> getMyInfo(@Parameter(hidden = true) @CurrentUserId Long userId) {
        UserInfoResponse response = userUsecase.getUserInfo(userId);

        return CommonResponse.success(GET_MY_INFO, response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "다른 사람 페이지 조회 API")
    public CommonResponse<UserInfoResponse> getUserInfo(@PathVariable long userId) {
        UserInfoResponse response = userUsecase.getUserInfo(userId);

        return CommonResponse.success(GET_USER_INFO, response);
    }

    @PatchMapping("/me/kakao-talk-id")
    @Operation(summary = "내 정보 수정 - 카카오톡 id")
    public CommonResponse<Void> updateKakaoTalkId(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                  @Valid @RequestBody KakaoTalkIdRequest request) {
        userUsecase.updateKakaoTalkId(userId, request);

        return CommonResponse.success(UPDATE_KAKAO_TALK_ID);
    }

    @PatchMapping("/me/profile-image")
    @Operation(summary = "내 정보 수정 - 프로필 사진")
    public CommonResponse<Void> updateProfileImage(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                   @Valid @RequestBody ProfileImageRequest request) {
        userUsecase.updateProfileImage(userId, request);

        return CommonResponse.success(UPDATE_PROFILE_IMAGE);
    }

    @PatchMapping("/me/introduction")
    @Operation(summary = "내 정보 수정 - 자기소개")
    public CommonResponse<Void> updateIntroduction(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                   @Valid @RequestBody IntroductionRequest request) {
        userUsecase.updateIntroduction(userId, request);

        return CommonResponse.success(UPDATE_INTRODUCTION);
    }

    @PatchMapping("/me/mbti")
    @Operation(summary = "내 정보 수정 - MBTI")
    public CommonResponse<Void> updateMbti(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @Valid @RequestBody MbtiRequest request) {
        userUsecase.updateMbti(userId, request);

        return CommonResponse.success(UPDATE_MBTI);
    }

    @PostMapping("/{userId}/block")
    @Operation(summary = "유저 차단하기 API")
    public CommonResponse<Void> blockUser(@Parameter(hidden = true) @CurrentUserId Long userId,
                                          @PathVariable("userId") long blockedUserId) {
        userUsecase.blockUser(userId, blockedUserId);

        return CommonResponse.success(BLOCK_USER);
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴 API")
    public CommonResponse<Void> deleteAccount(@Parameter(hidden = true) @CurrentUserId Long userId) {
        userUsecase.leave(userId);

        return CommonResponse.success(DELETE_USER);
    }
}
