package leets.leenk.domain.leenk.application.mapper;

import java.time.LocalDateTime;
import java.util.List;
import leets.leenk.domain.leenk.application.dto.response.LeenkAuthorResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipatedListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipatedResponse;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class LeenkParticipantsMapper {

    public LeenkParticipants toParticipants(Leenk leenk, User user, LocalDateTime joinedAt) {
        return LeenkParticipants.builder()
                .leenk(leenk)
                .participant(user)
                .joinedAt(joinedAt)
                .build();
    }

    public LeenkAuthorResponse toLeenkAuthorResponse(User user) {
        return LeenkAuthorResponse.builder()
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .name(user.getName())
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

    public LeenkParticipatedListResponse toLeenkParticipatedListResponse(Slice<LeenkParticipants> slice) {
        List<LeenkParticipatedResponse> responses = slice.getContent().stream()
                .map(leenkParticipants -> {
                    Leenk leenk = leenkParticipants.getLeenk();
                    User author = leenk.getAuthor();
                    LeenkAuthorResponse authorResponse = toLeenkAuthorResponse(author);

                    return LeenkParticipatedResponse.builder()
                            .id(leenk.getId())
                            .author(authorResponse)
                            .status(leenk.getStatus())
                            .title(leenk.getTitle())
                            .startTime(leenk.getStartTime())
                            .currentParticipants(leenk.getCurrentParticipants())
                            .maxParticipants(leenk.getMaxParticipants())
                            .build();
                })
                .toList();

        return new LeenkParticipatedListResponse(
                responses,
                PageableMapperUtil.from(slice)
        );
    }
}
