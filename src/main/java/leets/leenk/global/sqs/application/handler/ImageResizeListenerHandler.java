package leets.leenk.global.sqs.application.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import leets.leenk.domain.media.application.usecase.MediaUsecase;
import leets.leenk.global.sqs.application.dto.ImageResizeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageResizeListenerHandler {

    private final MediaUsecase mediaUsecase;
    private final ObjectMapper objectMapper;

    @SqsListener("${myapp.sqs.resize_queue_url}")
    public void imageResizeListener(String message){
        try {
            ImageResizeMessage imageResizeMessage = objectMapper.readValue(message, ImageResizeMessage.class);

            mediaUsecase.updateThumbnailUrl(imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());
            log.info("Successfully updated thumbnail originalUrl: {}, resizedUrl: {}",
                    imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());

        } catch (Exception e) {
            log.error("Failed update thumbnail message: {}", message, e);
        }
    }
}
