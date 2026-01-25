package leets.leenk.domain.feed.domain.repository

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LinkedUserRepository : JpaRepository<LinkedUser, Long> {
    @Query("SELECT lu FROM LinkedUser lu JOIN FETCH lu.user WHERE lu.feed = :feed order by lu.user.name asc")
    fun findAllByFeed(feed: Feed): List<LinkedUser>

    @Query(
        "SELECT lu.feed FROM LinkedUser lu JOIN lu.feed f JOIN FETCH f.user WHERE lu.user = :user AND f.user != :user AND f.deletedAt IS NULL ORDER BY f.createDate DESC",
    )
    fun findFeedsByLinkedUser(
        user: User,
        pageable: Pageable,
    ): Slice<Feed>

    fun deleteAllByFeed(feed: Feed)
}
