package leets.leenk.global.sqs.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageResizeMessage(
        @JsonProperty("originalUrl")
        String originalUrl,
        @JsonProperty("resizedUrl")
        String resizedUrl
) {
}
