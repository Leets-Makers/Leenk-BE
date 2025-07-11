package leets.leenk.global.sqs.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SqsMessageEvent {

	private final String title;
	private final String content;
	private final String fcmToken;

}
