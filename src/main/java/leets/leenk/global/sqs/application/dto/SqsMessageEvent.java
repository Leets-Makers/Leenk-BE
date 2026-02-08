package leets.leenk.global.sqs.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SqsMessageEvent {

	private final String title;
	private final String content;
	private final String fcmToken;
    private final String path;
    private final Long id;

    public SqsMessageEvent(String title, String content, String fcmToken, String path, Long id) {
        this.title = title;
        this.content = content;
        this.fcmToken = fcmToken;
        this.path = path;
        this.id = id;
    }

}
