package leets.leenk.global.config;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {
    public String[] getPublicUrl() {
        return new String[]{
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/health-check",
                "/kakao/login",
                "/apple/login",
                "/login",
                "/refresh"
        };
    }
}
