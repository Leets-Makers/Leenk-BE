package leets.leenk.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

	@Query("SELECT us.user FROM UserSetting us WHERE us.isNewFeedNotify = true " +
			"AND us.user.leaveDate IS NULL " +
			"AND us.user.id <> :userId")
	List<User> findAllActiveUsersWithNewFeedNotifyTrueExcludingUserId(Long userId);

    Optional<UserSetting> findByUser(User user);
}
