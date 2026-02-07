package leets.leenk.global.auth.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import leets.leenk.global.auth.application.dto.response.OauthErrorResponse;
import leets.leenk.global.auth.application.dto.response.OauthTokenResponse;
import leets.leenk.global.auth.application.exception.*;
import leets.leenk.global.auth.application.property.OauthProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * @deprecated Weeth OAuth 서버 의존 - 카카오 로그인 전용
 */
@Deprecated
@RequiredArgsConstructor
public abstract class AbstractOauthApiService {
    protected static final String USER_INACTIVE_ERROR = "WAE-001";
    protected static final String USER_NOT_FOUND_ERROR = "WAE-002";
    protected static final String INVALID_GRANT_ERROR = "invalid_grant";

    protected final OauthProperty oauthProperty;
    protected final RestClient authRestClient;
    protected final ObjectMapper objectMapper;

    /**
     * Provider별 Grant Type 반환 (예: "urn:ietf:params:oauth:grant-type:token-exchange")
     */
    protected abstract String getGrantType();

    /**
     * Provider별 Grant Type 파라미터 이름 반환 (예: "subject_token")
     */
    protected abstract String getGrantTypeName();

    public OauthTokenResponse getOauthToken(String accessToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", getGrantType());
        body.add("client_id", oauthProperty.getClientId());
        body.add("client_secret", oauthProperty.getClientSecret());
        body.add(getGrantTypeName(), accessToken);

        return authRestClient.post()
                .uri(oauthProperty.getOauthServerTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    handleErrorResponse(response.getBody(), false);
                })
                .body(OauthTokenResponse.class);
    }

    public OauthTokenResponse reissueOauthToken(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", oauthProperty.getClientId());
        body.add("client_secret", oauthProperty.getClientSecret());
        body.add("refresh_token", refreshToken);

        return authRestClient.post()
                .uri(oauthProperty.getOauthServerTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    handleErrorResponse(response.getBody(), true);
                })
                .body(OauthTokenResponse.class);
    }

    private void handleErrorResponse(InputStream body, boolean isReissue) {
        try {
            OauthErrorResponse error = objectMapper.readValue(body, OauthErrorResponse.class);

            if (isReissue) {
                switch (error.error()) {
                    case INVALID_GRANT_ERROR -> throw new RefreshTokenException();
                    default -> throw new OauthException(error.error_description());
                }
            } else {
                switch (error.error()) {
                    case USER_INACTIVE_ERROR -> throw new UserInActiveException(error.error_description());
                    case USER_NOT_FOUND_ERROR -> throw new UnRegisterUserException();
                    default -> throw new OauthException(error.error_description());
                }
            }
        } catch (JsonProcessingException e) {
            throw new CustomJsonProcessingException(e.getMessage());
        } catch (IOException e) {
            throw new OauthException("Failed to read error response: " + e.getMessage());
        }
    }
}
