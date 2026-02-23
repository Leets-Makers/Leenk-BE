package leets.leenk.domain.media.domain.repository

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.media.domain.entity.Media
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MediaRepository : JpaRepository<Media, Long> {
    fun findAllByFeedInOrderByPosition(feeds: List<Feed>): List<Media>

    fun findAllByFeedOrderByPosition(feed: Feed): List<Media>

    fun findAllByLeenkOrderByPosition(leenk: Leenk): List<Media>

    fun findFirstByLeenkOrderByPositionAsc(leenk: Leenk): Optional<Media>

    fun findAllByLeenkInOrderByPosition(leenks: List<Leenk>): List<Media>

    fun deleteAllByFeed(feed: Feed)

    fun findByMediaUrl(originalUrl: String): Optional<Media>
}
