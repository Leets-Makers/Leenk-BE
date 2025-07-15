package leets.leenk.domain.notification.domain.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApnsNotificationService {

    @Value("${apns.team-id}")
    private String teamId;

    @Value("${apns.key-id}")
    private String keyId;

    @Value("${apns.bundle-id}")
    private String bundleId;

    @Value("${apns.key-path}")
    private String keyPath;

    @Value("${apns.production}")
    private boolean production;

    private ApnsClient apnsClient;

    @PostConstruct
    public void init() throws Exception {
        try {
            InputStream inputStream = getInputStream(keyPath);

            ApnsSigningKey signingKey = ApnsSigningKey.loadFromInputStream(
                    inputStream, teamId, keyId
            );

            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(production ? ApnsClientBuilder.PRODUCTION_APNS_HOST :
                            ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(signingKey)
                    .build();


        } catch (IOException e) {
            log.warn("APNs 설정 중 예외 발생", e);
        }

    }

    public void sendPush(String deviceToken, String title, String body) {
        String payload = new SimpleApnsPayloadBuilder()
                .setAlertTitle("Leenk")
                .setAlertBody(body)
                .setSound("default")
                .build();

        SimpleApnsPushNotification notification = new SimpleApnsPushNotification(
                deviceToken,
                bundleId,
                payload,
                Instant.now().plusSeconds(3600)
        );

        apnsClient.sendNotification(notification).whenComplete((response, cause) -> {
            if (response != null && response.isAccepted()) {
                log.info("✅ 푸시 전송 성공!");
            } else {
                log.info("❌ 푸시 실패: " +
                        (response != null ? response.getRejectionReason() : cause.getMessage()));
                log.info(deviceToken);
            }
        });
    }

    private InputStream getInputStream(String path) throws IOException {
        if (new File(path).exists()) {
            return new FileInputStream(path);
        } else {
            return new ClassPathResource(path).getInputStream();
        }
    }
}
