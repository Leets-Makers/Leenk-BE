package leets.leenk.domain.user.presentation;

import leets.leenk.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode implements ResponseCodeInterface {

    GET_MY_INFO(1100, HttpStatus.OK, "내 정보 조회에 성공했습니다."),
    GET_USER_INFO(1101, HttpStatus.OK, "다른 사용자 정보 조회에 성공했습니다."),
    UPDATE_PROFILE_IMAGE(1102, HttpStatus.OK, "프로필 이미지 수정에 성공했습니다."),
    UPDATE_INTRODUCTION(1103, HttpStatus.OK, "자기소개 수정에 성공했습니다."),
    UPDATE_MBTI(1104, HttpStatus.OK, "MBTI 수정에 성공했습니다."),
    UPDATE_KAKAO_TALK_ID(1105, HttpStatus.OK, "카카오톡 Id 수정에 성공했습니다."),
    COMPLETE_PROFILE(1106, HttpStatus.OK, "기본 정보 입력에 성공했습니다."),
    DELETE_USER(1107, HttpStatus.OK, "회원 탈퇴에 성공했습니다."),
    BLOCK_USER(1108, HttpStatus.OK, "사용자 차단에 성공했습니다."),
    UPDATE_AGREEMENT(1109, HttpStatus.OK, "약관 동의 입력에 성공했습니다."),

    GET_NOTIFICATION_SETTING(1110, HttpStatus.OK, "알림 설정 조회에 성공했습니다."),
    UPDATE_NOTIFICATION_SETTING(1111, HttpStatus.OK, "알림 설정 수정에 성공했습니다."),
    SEND_FEEDBACK(1112, HttpStatus.OK, "의견 남기기에 성공했습니다."),
    UPDATE_FCM_TOKEN(1113, HttpStatus.OK, "fcm 토큰 수정에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
