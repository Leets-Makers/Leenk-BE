package leets.leenk.global.config;

import org.springframework.stereotype.Component;

/**
 * 인증 없이 접근 가능한 공개 URL 목록

 * Note: /kakao/login은 의도적으로 제외됨 (@Deprecated, Weeth 의존성 제거)
 */
@Component
public class PermitUrlConfig {
    public String[] getPublicUrl() {
        return new String[]{
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/health-check",
                "/apple/login",
                "/refresh"
        };
    }
}
