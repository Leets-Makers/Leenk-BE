package leets.leenk.domain.notification.application.mapper;

import leets.leenk.domain.notification.domain.entity.leenkContent.NewLeenkParticipantDetail;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NewLeenkParticipantMapper {

    public NewLeenkParticipantDetail toNewLeenkParticipantDetail(User newUser) {
        return NewLeenkParticipantDetail.builder()
                .participantId(newUser.getId())
                .participantName(newUser.getName())
                .build();
    }
}
