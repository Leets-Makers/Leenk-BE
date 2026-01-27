package leets.leenk.global.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leets.leenk.domain.birthday.application.exception.BirthdayErrorCode;
import leets.leenk.domain.feed.application.exception.FeedErrorCode;
import leets.leenk.domain.leenk.application.exception.LeenkErrorCode;
import leets.leenk.domain.media.application.exception.MediaErrorCode;
import leets.leenk.domain.notification.application.exception.NotificationErrorCode;
import leets.leenk.domain.user.application.exception.UserErrorCode;
import leets.leenk.global.auth.application.exception.AuthErrorCode;
import leets.leenk.global.common.exception.ApiErrorCodeExample;
import leets.leenk.global.common.exception.CommonErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 예외 코드 문서화를 위한 컨트롤러
 * 실제 비즈니스 로직을 수행하지 않고, Swagger 문서에 각 도메인별 예외 정보를 표시하기 위한 목적으로만 사용됩니다.
 * 각 엔드포인트는 해당 도메인에서 발생할 수 있는 모든 예외 케이스를 Swagger UI에서 확인할 수 있도록 합니다.
 */
@RestController
@RequestMapping("/api/v1/docs/exceptions")
@Tag(name = "Exception Document", description = "API 에러 코드 문서")
public class ExceptionDocController {

    @GetMapping("/auth")
    @Operation(
            summary = "인증 관련 예외 목록",
            description = "인증 및 권한 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(AuthErrorCode.class)
    public void authErrorCodes() {
    }

    @GetMapping("/user")
    @Operation(
            summary = "사용자 관련 예외 목록",
            description = "사용자 조회, 수정, 차단 등 사용자 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(UserErrorCode.class)
    public void userErrorCodes() {
    }

    @GetMapping("/feed")
    @Operation(
            summary = "피드 및 댓글 관련 예외 목록",
            description = "피드 조회, 작성, 수정, 삭제 및 댓글 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(FeedErrorCode.class)
    public void feedErrorCodes() {
    }

    @GetMapping("/notification")
    @Operation(
            summary = "알림 관련 예외 목록",
            description = "알림 조회, 읽음 처리 등 알림 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(NotificationErrorCode.class)
    public void notificationErrorCodes() {
    }

    @GetMapping("/leenk")
    @Operation(
            summary = "링크 관련 예외 목록",
            description = "링크 생성, 참여, 마감, 종료 등 링크 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(LeenkErrorCode.class)
    public void leenkErrorCodes() {
    }

    @GetMapping("/media")
    @Operation(
            summary = "미디어 관련 예외 목록",
            description = "미디어 업로드, 조회 등 미디어 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(MediaErrorCode.class)
    public void mediaErrorCodes() {
    }

    @GetMapping("/birthday")
    @Operation(
            summary = "생일 관련 예외 목록",
            description = "생일 조회, 축하 메시지 전송 등 생일 관련 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(BirthdayErrorCode.class)
    public void birthdayErrorCodes() {
    }

    @GetMapping("/common")
    @Operation(
            summary = "공통 예외 목록",
            description = "서버 에러, 클라이언트 요청 에러 등 공통 예외 코드를 확인할 수 있습니다."
    )
    @ApiErrorCodeExample(CommonErrorCode.class)
    public void commonErrorCodes() {
    }
}
