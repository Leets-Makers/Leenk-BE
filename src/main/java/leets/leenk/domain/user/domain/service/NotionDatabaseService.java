package leets.leenk.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotionDatabaseService {

    private static final String NOTION_INSERT_URI = "https://api.notion.com/v1/pages/";

    @Value("${notion.token}")
    private String NOTION_TOKEN;

    @Value("${notion.feedback_database_id}")
    private String FEEDBACK_DATABASE_ID;

    @Value("${notion.report_database_id}")
    private String REPORT_DATABASE_ID;

    @Value("${notion.version}")
    private String NOTION_VERSION;

    private final RestClient notionRestClient;

    @Async
    public void sendFeedback(String feedback) {
        String today = LocalDate.now().toString();

        Map<String, Object> requestBody = Map.of(
                "parent", Map.of(
                        "type", "database_id",
                        "database_id", FEEDBACK_DATABASE_ID
                ),
                "properties", Map.of(
                        "피드백", Map.of(
                                "title", List.of(
                                        Map.of("text", Map.of("content", feedback))
                                )
                        ),
                        "날짜", Map.of(
                                "date", Map.of("start", today)
                        )
                )
        );

        notionRestClient.post()
                .uri(NOTION_INSERT_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + NOTION_TOKEN)
                .header("Notion-Version", NOTION_VERSION)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }

    @Async
    public void sendFeedReport(String report, long userId, long feedId) {
        String today = LocalDate.now().toString();

        Map<String, Object> requestBody = Map.of(
                "parent", Map.of(
                        "type", "database_id",
                        "database_id", REPORT_DATABASE_ID
                ),
                "properties", Map.of(
                        "사유", Map.of(
                                "title", List.of(
                                        Map.of("text", Map.of("content", report))
                                )
                        ),
                        "신고 날짜", Map.of(
                                "date", Map.of("start", today)
                        ),
                        "피드 id", Map.of(
                                "rich_text", List.of(
                                        Map.of("text", Map.of("content", String.valueOf(feedId)))
                                )
                        ),
                        "사용자 id", Map.of(
                                "rich_text", List.of(
                                        Map.of("text", Map.of("content", String.valueOf(userId)))
                                )
                        )
                )
        );

        notionRestClient.post()
                .uri(NOTION_INSERT_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + NOTION_TOKEN)
                .header("Notion-Version", NOTION_VERSION)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
