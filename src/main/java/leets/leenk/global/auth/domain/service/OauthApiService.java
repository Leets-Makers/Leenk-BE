package leets.leenk.global.auth.domain.service;

import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import leets.leenk.global.auth.application.property.OauthProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * @deprecated Weeth OAuth 서버 의존 - 카카오 로그인 전용
 */
@Deprecated
@Service
@RequiredArgsConstructor
public class OauthApiService {

    private final OauthProperty oauthProperty;
    private final RestClient authRestClient;

    public OauthUserInfoResponse getUserInfo(String accessToken) {
        return authRestClient.get()
                .uri(oauthProperty.getOauthServerUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(OauthUserInfoResponse.class);
    }
}
