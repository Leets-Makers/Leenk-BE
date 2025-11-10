package leets.leenk.global.auth.application.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.oauth2.apple")
public class AppleOauthProperty {
    private String appleGrantType;
    private String appleGrantTypeName;
}
