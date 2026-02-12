package leets.leenk.global.sqs.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import leets.leenk.global.sqs.application.mapper.AwsSqsManager;
import leets.leenk.global.sqs.application.dto.SqsMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsEventHandler {

	private final AwsSqsManager awsSqsManager;

	@EventListener
	public void handleEvent(SqsMessageEvent event) {
		try {
			SendMessageRequest request = awsSqsManager.createPushAlarmMessageRequest(event);
			awsSqsManager.sendMessageToQueue(request);
		} catch (Exception e){
			log.error("SQS 메세지 전송 실패 : {}", e.getMessage());
		}
	}
}
