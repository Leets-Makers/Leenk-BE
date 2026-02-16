package leets.leenk.domain.media.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.media.domain.entity.enums.MediaType

@Entity
@Table(
    name = "medias",
    indexes = [
        Index(name = "idx_feed_position", columnList = "feed_id, position"),
        Index(name = "idx_leenk_position", columnList = "leenk_id, position"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_feed_position", columnNames = ["feed_id", "position"]),
        UniqueConstraint(name = "uk_leenk_position", columnNames = ["leenk_id", "position"]),
    ],
)
class Media(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var mediaUrl: String,
    @Column(nullable = false)
    var thumbnailUrl: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val mediaType: MediaType,
    @Column(nullable = false)
    val position: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", updatable = false)
    val feed: Feed? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leenk_id", updatable = false)
    val leenk: Leenk? = null,
) {
    fun updateMediaUrl(mediaUrl: String) {
        this.mediaUrl = mediaUrl
        this.thumbnailUrl = mediaUrl
    }

    fun updateThumbnailUrl(thumbnailUrl: String) {
        this.thumbnailUrl = thumbnailUrl
    }
}
