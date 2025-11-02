package leets.leenk.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

	@Query("SELECT us.user FROM UserSetting us WHERE us.isNewFeedNotify = true " +
			"AND us.user.leaveDate IS NULL " +
			"AND us.user.id <> :authorUserId")
	List<User> findAllActiveUsersWithNewFeedNotifyTrueExcludingUserId(@Param("authorUserId") Long authorUserId);

    Optional<UserSetting> findByUser(User user);

    @Query("SELECT us.user FROM UserSetting us WHERE us.isNewLeenkNotify = true " +
            "AND us.user.leaveDate IS NULL " +
            "AND us.user.id <> :authorUserId")
    List<User> findAllActiveUsersWithNewLeenkNotifyTrueExcludingUserId(@Param("authorUserId") Long authorUserId);

    @Query("SELECT us.user FROM UserSetting us WHERE us.isBirthdayNotify = true " +
            "AND us.user.leaveDate IS NULL")
    List<User> findAllActiveUsersWithBirthdayNotifyTrue();
}
