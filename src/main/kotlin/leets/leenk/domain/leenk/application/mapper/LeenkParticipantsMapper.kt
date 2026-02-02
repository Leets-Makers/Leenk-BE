package leets.leenk.domain.leenk.application.mapper;

import java.time.LocalDateTime;
import java.util.List;

import leets.leenk.domain.leenk.application.dto.response.LeenkAuthorResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeenkParticipantsMapper {
    private final UserProfileMapper userProfileMapper;

    public LeenkParticipants toParticipants(Leenk leenk, User user, LocalDateTime joinedAt) {
        return LeenkParticipants.builder()
                .leenk(leenk)
                .participant(user)
                .joinedAt(joinedAt)
                .build();
    }

    public LeenkAuthorResponse toLeenkAuthorResponse(User user) {
        return LeenkAuthorResponse.builder()
                .user(userProfileMapper.toProfile(user))
                .build();
    }

    public LeenkParticipantsListResponse toLeenkParticipantsListResponse(Leenk leenk,
                                                                         List<LeenkParticipants> participants) {
        List<LeenkParticipantResponse> responses = participants.stream()
                .map(leenkParticipants -> {
                    User participantUser = leenkParticipants.getParticipant();

                    return LeenkParticipantResponse.builder()
                            .participant(toLeenkAuthorResponse(participantUser))
                            .kakaoTalkId(participantUser.getKakaoTalkId())
                            .currentParticipants(leenk.getCurrentParticipants())
                            .maxParticipants(leenk.getMaxParticipants())
                            .joinedAt(leenkParticipants.getJoinedAt())
                            .isHost(participantUser.getId().equals(leenk.getAuthor().getId()))
                            .build();
                })
                .toList();

        return new LeenkParticipantsListResponse(responses);
    }
}
