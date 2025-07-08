package leets.leenk.domain.user.application.mapper;

import leets.leenk.domain.user.domain.entity.UserBlock;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserBlockMapper {

    public UserBlock toUserBlock(User blocker, User blocked) {
        return UserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
    }
}
