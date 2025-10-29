package leets.leenk.global.sqs.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import leets.leenk.domain.media.domain.entity.enums.DomainType;

public record ImageResizeMessage(
        @JsonProperty("domainType")
        DomainType domainType,
        @JsonProperty("originalUrl")
        String originalUrl,
        @JsonProperty("resizedUrl")
        String resizedUrl
) {
}
