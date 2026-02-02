package leets.leenk.domain.leenk.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeenkReportRequest(

        @NotBlank
        @Size(max = 100)
        String report
) {
}
