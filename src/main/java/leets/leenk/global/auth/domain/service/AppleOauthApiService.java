package leets.leenk.global.auth.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import leets.leenk.global.auth.application.property.AppleOauthProperty;
import leets.leenk.global.auth.application.property.OauthProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AppleOauthApiService extends AbstractOauthApiService {
    private final AppleOauthProperty appleOauthProperty;

    public AppleOauthApiService(OauthProperty oauthProperty,
                                AppleOauthProperty appleOauthProperty,
                                RestClient authRestClient,
                                ObjectMapper objectMapper) {
        super(oauthProperty, authRestClient, objectMapper);
        this.appleOauthProperty = appleOauthProperty;
    }

    @Override
    protected String getGrantType() {
        return appleOauthProperty.getAppleGrantType();
    }

    @Override
    protected String getGrantTypeName() {
        return appleOauthProperty.getAppleGrantTypeName();
    }
}
