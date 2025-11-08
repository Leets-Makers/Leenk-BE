package leets.leenk.global.sqs.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import leets.leenk.domain.media.domain.entity.enums.DomainType;

public record ImageResizeMessage(
        DomainType domainType,
        String originalUrl,
        String resizedUrl
) {
}
