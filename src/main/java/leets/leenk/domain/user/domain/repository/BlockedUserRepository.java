package leets.leenk.domain.user.domain.repository;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockedUserRepository extends JpaRepository<UserBlock, Long> {
    List<UserBlock> findAllByBlocker(User blocker);

    Optional<UserBlock> findByBlockerAndBlocked(User blocker, User blocked);
}
