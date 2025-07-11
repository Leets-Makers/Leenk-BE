package leets.leenk.global.sqs.application.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import leets.leenk.global.sqs.application.dto.SqsMessageEvent;
import leets.leenk.global.sqs.config.AwsSqsProperties;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
public class AwsSqsManager {

	private final AwsSqsProperties awsSqsProperties;
	private final SqsAsyncClient sqsAsyncClient;

	public void sendMessageToQueue(SendMessageRequest event) {
		sqsAsyncClient.sendMessage(event);
	}

	public SendMessageRequest createPushAlarmMessageRequest(SqsMessageEvent event) {
		return SendMessageRequest.builder()
			.queueUrl(awsSqsProperties.getQueueUrl())
			.delaySeconds(awsSqsProperties.getMessageDelaySecs())
			.messageBody(event.getContent())
			.messageAttributes(Map.of(
				"title", convertToAttributeValue(event.getTitle()),
				"fcmToken", convertToAttributeValue(event.getFcmToken())
			))
			.build();
	}


	private MessageAttributeValue convertToAttributeValue(String value) {
		return MessageAttributeValue.builder()
			.dataType("String")
			.stringValue(value)
			.build();
	}
}
