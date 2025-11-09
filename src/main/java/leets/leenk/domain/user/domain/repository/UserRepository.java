package leets.leenk.domain.user.domain.repository;

import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query(value = """
            WITH t AS (
              SELECT
                u.*,
                CASE
                  WHEN DATE_FORMAT(u.birthday, '%m-%d') > DATE_FORMAT(:today, '%m-%d')
                    THEN STR_TO_DATE(CONCAT(YEAR(:today), '-', DATE_FORMAT(u.birthday, '%m-%d')), '%Y-%m-%d')
                  ELSE STR_TO_DATE(CONCAT(YEAR(:today) + 1, '-', DATE_FORMAT(u.birthday, '%m-%d')), '%Y-%m-%d')
                END AS next_birthday FROM users u WHERE u.birthday IS NOT NULL
            )
            SELECT t.* FROM t WHERE DATEDIFF(t.next_birthday, :today) BETWEEN 1 AND :days ORDER BY DATEDIFF(t.next_birthday, :today) ASC
            """, nativeQuery = true)
    List<User> findUpcomingBirthdays(@Param("today") LocalDate today, @Param("days") int days);
}
