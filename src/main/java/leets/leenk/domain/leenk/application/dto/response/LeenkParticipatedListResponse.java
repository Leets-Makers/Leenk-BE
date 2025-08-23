package leets.leenk.domain.leenk.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import leets.leenk.global.common.dto.CommonPageableResponse;

public record LeenkParticipatedListResponse(

        @Schema(description = "참여한 링크 목록 (마이페이지)")
        List<LeenkParticipatedResponse> participants,

        CommonPageableResponse pageable
) {
}
