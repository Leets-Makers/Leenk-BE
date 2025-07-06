package leets.leenk.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackWebhookService {

    @Value("${slack.webhook_url}")
    private String webhookUrl;

    private final RestClient slackRestClient;

    public void sendFeedback(String feedback) {
        String formatted = "*[링크 서비스 피드백]*\n```" + feedback + "```";

        slackRestClient.post()
                .uri(webhookUrl)
                .header("Content-Type", "application/json")
                .body(Map.of("text", formatted))
                .retrieve()
                .toBodilessEntity();
    }

    public void sendFeedReport(String report) {
        String formatted = "*[피드 신고]*\n```" + report + "```";

        slackRestClient.post()
                .uri(webhookUrl)
                .header("Content-Type", "application/json")
                .body(Map.of("text", formatted))
                .retrieve()
                .toBodilessEntity();
    }
}
