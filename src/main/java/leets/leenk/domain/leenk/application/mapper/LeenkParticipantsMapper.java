package leets.leenk.domain.leenk.application.mapper;

import java.util.List;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LeenkParticipantsMapper {

    public LeenkParticipants toParticipants(Leenk leenk, User user) {
        return LeenkParticipants.builder()
                .leenk(leenk)
                .participant(user)
                .build();
    }

    public LeenkParticipantsListResponse toLeenkParticipantsListResponse(
            Leenk leenk,
            List<LeenkParticipants> participants
    ) {
        List<LeenkParticipantResponse> responses = participants.stream()
                .map(leenkParticipants -> LeenkParticipantResponse.builder()
                        .id(leenk.getId())
                        .userId(leenkParticipants.getParticipant().getId())
                        .userName(leenkParticipants.getParticipant().getName())
                        .joinedAt(leenkParticipants.getJoinedAt())
                        .isHost(leenkParticipants.getParticipant().getId().equals(leenk.getAuthor().getId()))
                        .createdAt(leenkParticipants.getCreateDate())
                        .updatedAt(leenkParticipants.getUpdateDate())
                        .build())
                .toList();
        return new LeenkParticipantsListResponse(responses);
    }
}
