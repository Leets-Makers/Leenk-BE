package leets.leenk.domain.feed.domain.repository;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query("SELECT f FROM Feed f JOIN FETCH f.user u WHERE f.deletedAt IS NULL AND u.id NOT IN :blockedUserIds")
    Slice<Feed> findAllByDeletedAtIsNullWithUser(Pageable pageable, List<Long> blockedUserIds);

    @Query("SELECT f FROM Feed f JOIN FETCH f.user WHERE f.deletedAt IS NULL AND f.user = :user")
    Slice<Feed> findAllByUserAndDeletedAtIsNull(User user, Pageable pageable);

    @Query("SELECT f FROM Feed f JOIN FETCH f.user WHERE f.deletedAt IS NULL AND f.id = :id")
    Optional<Feed> findByDeletedAtIsNullAndId(Long id);
}
