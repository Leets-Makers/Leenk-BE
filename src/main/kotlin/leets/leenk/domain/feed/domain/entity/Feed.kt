package leets.leenk.domain.feed.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(
    name = "feeds",
    indexes = [
        // 피드 네비게이션 조회 최적화: createDate 기준 정렬 + deletedAt 필터링
        Index(name = "idx_feeds_created_deleted", columnList = "create_date, deleted_at"),
        // 사용자별 피드 조회 최적화: user_id + createDate 정렬
        Index(name = "idx_feeds_user_created", columnList = "user_id, create_date"),
    ],
)
class Feed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(length = 100)
    var description: String? = null,
    @Column(nullable = false)
    var totalReactionCount: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {
    fun increaseTotalReactionCount(reactionCount: Long) {
        this.totalReactionCount += reactionCount
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    val requireId: Long
        get() = checkNotNull(id) { "영속화되지 않은 Feed 엔티티입니다" }
}
