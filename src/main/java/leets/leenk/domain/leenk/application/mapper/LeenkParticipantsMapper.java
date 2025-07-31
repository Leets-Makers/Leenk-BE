package leets.leenk.domain.leenk.application.mapper;

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
}
