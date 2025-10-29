package leets.leenk.global.sqs.application.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import leets.leenk.domain.media.application.exception.MediaNotFoundException;
import leets.leenk.domain.media.application.usecase.MediaUsecase;
import leets.leenk.domain.media.domain.entity.enums.DomainType;
import leets.leenk.domain.user.application.usecase.UserUsecase;
import leets.leenk.global.sqs.application.dto.ImageResizeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageResizeListenerHandler {

    private final MediaUsecase mediaUsecase;
    private final UserUsecase userUsecase;
    private final ObjectMapper objectMapper;

    @SqsListener("${myapp.sqs.resize_queue_url}")
    public void imageResizeListener(String message){
        try {
            ImageResizeMessage imageResizeMessage = objectMapper.readValue(message, ImageResizeMessage.class);
            DomainType domainType = imageResizeMessage.domainType();
            switch (domainType) {
                case PROFILE -> {
                    userUsecase.updateThumbnailUrl(imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());
                    log.info("Updated user thumbnail originalUrl: {}, resizedUrl: {}",
                            imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());
                }
                case FEED, LEENK -> {
                    mediaUsecase.updateThumbnailUrl(imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());
                    log.info("Updated media updated thumbnail originalUrl: {}, resizedUrl: {}",
                            imageResizeMessage.originalUrl(), imageResizeMessage.resizedUrl());
                }
                default -> {
                    log.warn("Unhandled domainType: {}", domainType);
                }
            }
        } catch (JsonProcessingException e) {

            log.error("Failed to parse SQS message: {}", message, e);
        } catch (MediaNotFoundException e) {

            log.error("Media not found for message: {}", message, e);
        } catch (Exception e) {

            log.error("Failed update thumbnail message: {}", message, e);
        }
    }
}
