package leets.leenk.domain.feed.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedReportRequest(
        @NotBlank
        @Size(max = 100)
        String report
) {
}
