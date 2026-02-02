package leets.leenk.domain.leenk.application.dto.response;

import java.util.List;

public record LeenkParticipantsListResponse(
        List<LeenkParticipantResponse> participants
) {
}
