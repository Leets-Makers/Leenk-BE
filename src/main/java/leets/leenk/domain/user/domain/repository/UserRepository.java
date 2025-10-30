package leets.leenk.domain.user.domain.repository;

import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
                SELECT u
                FROM User u
                WHERE u.leaveDate IS NULL
                  AND u.deleteDate IS NULL
                  AND u.birthday IS NOT NULL
                  AND month(u.birthday) = :month
                  AND day(u.birthday) = :day
                ORDER BY u.name
            """)
    List<User> findAllUsersInBirthday(@Param("month") int month, @Param("day") int day);
}
