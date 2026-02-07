package leets.leenk.global.auth.application.dto.response;

/**
 * @deprecated Weeth OAuth 서버 응답 DTO - 카카오 로그인 전용
 */
@Deprecated
public record OauthUserInfoResponse(
        long userId,
        String name,
        int cardinal,
        String position
) {
}
