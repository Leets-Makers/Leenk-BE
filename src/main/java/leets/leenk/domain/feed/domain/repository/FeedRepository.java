package leets.leenk.domain.feed.domain.repository;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query("SELECT f FROM Feed f JOIN FETCH f.user u WHERE f.deletedAt IS NULL AND u.id NOT IN :blockedUserIds ORDER BY f.createDate DESC")
    Slice<Feed> findAllByDeletedAtIsNullWithUser(Pageable pageable, List<Long> blockedUserIds);

    @Query("SELECT f FROM Feed f JOIN FETCH f.user WHERE f.deletedAt IS NULL AND f.user = :user ORDER BY f.createDate DESC")
    Slice<Feed> findAllByUserAndDeletedAtIsNull(User user, Pageable pageable);

    @Query("SELECT f FROM Feed f JOIN FETCH f.user WHERE f.deletedAt IS NULL AND f.id = :id")
    Optional<Feed> findByDeletedAtIsNullAndId(Long id);

    /**
     * 현재 피드보다 최신인 피드 조회 (이전 피드)
     * createDate > currentCreateDate 조건으로 더 최근 피드를 ASC 정렬로 조회
     */
    @Query("SELECT f FROM Feed f JOIN FETCH f.user u " +
           "WHERE f.createDate > :currentCreateDate " +
           "AND f.deletedAt IS NULL " +
           "AND (:blockedUserIds IS NULL OR u.id NOT IN :blockedUserIds) " +
           "ORDER BY f.createDate ASC")
    List<Feed> findPrevFeeds(
            @Param("currentCreateDate") LocalDateTime currentCreateDate,
            @Param("blockedUserIds") List<Long> blockedUserIds,
            Pageable pageable
    );

    /**
     * 현재 피드보다 오래된 피드 조회 (다음 피드)
     * createDate < currentCreateDate 조건으로 더 오래된 피드를 DESC 정렬로 조회
     */
    @Query("SELECT f FROM Feed f JOIN FETCH f.user u " +
           "WHERE f.createDate < :currentCreateDate " +
           "AND f.deletedAt IS NULL " +
           "AND (:blockedUserIds IS NULL OR u.id NOT IN :blockedUserIds) " +
           "ORDER BY f.createDate DESC")
    List<Feed> findNextFeeds(
            @Param("currentCreateDate") LocalDateTime currentCreateDate,
            @Param("blockedUserIds") List<Long> blockedUserIds,
            Pageable pageable
    );
}
