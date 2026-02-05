package leets.leenk.global.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Apple 로그인 요청
 *
 * @param idToken  Apple ID Token (idToken 또는 authCode 중 하나 필수)
 * @param authCode Apple Authorization Code (idToken 또는 authCode 중 하나 필수)
 * @param name     최초 로그인 시 Apple에서 제공한 사용자 이름 (선택)
 *                 - Apple은 최초 인증 시에만 이름을 제공하므로 클라이언트에서 전달
 *                 - 이후 로그인에서는 null 전달 가능
 */
public record AppleLoginRequest(
        @Schema(description = "Apple Identity Token (authCode와 함께 사용 불가)", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ik...", nullable = true)
        String idToken,

        @Schema(description = "Apple Authorization Code (로컬 테스트용)", example = "xxxxx4acxx1f.0.sxxu.xxx", nullable = true)
        String authCode,

        @Schema(description = "사용자 이름 (최초 로그인 시에만 Apple이 제공)", example = "홍길동", nullable = true)
        String name
) {
}
