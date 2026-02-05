package leets.leenk.global.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Apple 로그인 요청
 *
 * @param idToken Apple ID Token (필수)
 * @param name    최초 로그인 시 Apple에서 제공한 사용자 이름 (선택)
 *                - Apple은 최초 인증 시에만 이름을 제공하므로 클라이언트에서 전달
 *                - 이후 로그인에서는 null 전달 가능
 */
public record AppleLoginRequest(
        @NotBlank(message = "Apple ID Token은 필수입니다")
        @Schema(description = "Apple Identity Token", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ik...")
        String idToken,

        @Schema(description = "사용자 이름 (최초 로그인 시에만 Apple이 제공)", example = "홍길동", nullable = true)
        String name
) {
}