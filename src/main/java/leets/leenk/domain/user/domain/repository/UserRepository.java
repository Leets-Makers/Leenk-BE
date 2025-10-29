package leets.leenk.domain.user.domain.repository;

import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndLeaveDateIsNullAndDeleteDateIsNull(long userId);

    List<User> findAllByIdInAndLeaveDateIsNullAndDeleteDateIsNull(List<Long> userIds);

    List<User> findAllByLeaveDateIsNullAndDeleteDateIsNullOrderByName();

    Slice<User> findAllByLeaveDateIsNullAndDeleteDateIsNullOrderByName(Pageable pageable);

    List<User> findByDeleteDateIsNullAndLeaveDateBefore(LocalDateTime threshold);

    Optional<User> findByName(String name);

    Optional<User> findByProfileImage(String profileImage);
}
