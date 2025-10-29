package leets.leenk.domain.user.application.mapper;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBackupInfo;
import org.springframework.stereotype.Component;

@Component
public class UserBackupInfoMapper {

    public UserBackupInfo toUserBackupInfo(User user) {
        return UserBackupInfo.builder()
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .thumbnail(user.getThumbnail())
                .name(user.getName())
                .build();
    }
}
