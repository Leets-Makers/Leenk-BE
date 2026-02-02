package leets.leenk.domain.leenk.application.dto.response;

import java.util.List;
import leets.leenk.global.common.dto.CommonPageableResponse;
import lombok.Builder;

@Builder
public record LeenkListResponse(
        List<LeenkResponse> leenks,
        CommonPageableResponse pageable
) {
}
