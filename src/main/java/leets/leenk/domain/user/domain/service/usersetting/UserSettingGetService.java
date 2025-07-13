package leets.leenk.domain.user.domain.service.usersetting;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSettingGetService {
	private final UserSettingRepository userSettingRepository;

	public List<User> getUsersToNotifyNewFeed(Long userId) {
		return userSettingRepository.findAllActiveUsersWithNewFeedNotifyTrueExcludingUserId(userId);
	}

    public Optional<UserSetting> findByUser(User user) {
        return userSettingRepository.findByUser(user);
    }
}
