package leets.leenk.domain.leenk.domain.entity

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
import jakarta.validation.constraints.Size
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(
    name = "leenks",
    indexes = [
        Index(name = "idx_leenks_status_start_time", columnList = "status, start_time"),
    ],
)
class Leenk(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leenk_id")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val author: User,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    val location: Location,
    @field:Size(max = 30)
    @Column(nullable = false, length = 30)
    var title: String,
    @field:Size(max = 200)
    @Column(length = 200)
    var content: String? = null,
    @Column(nullable = false)
    var startTime: LocalDateTime,
    @Column(nullable = false)
    var maxParticipants: Long,
    @Column(nullable = false)
    var currentParticipants: Long = 1L,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: LeenkStatus = LeenkStatus.RECRUITING,
) : BaseEntity() {
    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String?) {
        this.content = content
    }

    fun updateStartTime(startTime: LocalDateTime) {
        this.startTime = startTime
    }

    fun updateMaxParticipants(maxParticipants: Long) {
        this.maxParticipants = maxParticipants
    }

    fun changeStatusToClosed() {
        this.status = LeenkStatus.CLOSED
    }

    fun changeStatusToFinished() {
        this.status = LeenkStatus.FINISHED
    }

    fun increaseCurrentParticipants() {
        this.currentParticipants++
    }

    fun decreaseCurrentParticipants() {
        this.currentParticipants--
    }

    companion object {
        @JvmStatic
        fun builder(): LeenkBuilder = LeenkBuilder()
    }

    class LeenkBuilder {
        private var id: Long? = null
        private var author: User? = null
        private var location: Location? = null
        private var title: String = ""
        private var content: String? = null
        private var startTime: LocalDateTime? = null
        private var maxParticipants: Long = 0L
        private var currentParticipants: Long = 1L
        private var status: LeenkStatus = LeenkStatus.RECRUITING

        fun id(id: Long?): LeenkBuilder {
            this.id = id
            return this
        }

        fun author(author: User): LeenkBuilder {
            this.author = author
            return this
        }

        fun location(location: Location): LeenkBuilder {
            this.location = location
            return this
        }

        fun title(title: String): LeenkBuilder {
            this.title = title
            return this
        }

        fun content(content: String?): LeenkBuilder {
            this.content = content
            return this
        }

        fun startTime(startTime: LocalDateTime): LeenkBuilder {
            this.startTime = startTime
            return this
        }

        fun maxParticipants(maxParticipants: Long): LeenkBuilder {
            this.maxParticipants = maxParticipants
            return this
        }

        fun currentParticipants(currentParticipants: Long): LeenkBuilder {
            this.currentParticipants = currentParticipants
            return this
        }

        fun status(status: LeenkStatus): LeenkBuilder {
            this.status = status
            return this
        }

        fun build(): Leenk =
            Leenk(
                id = id,
                author = author!!,
                location = location!!,
                title = title,
                content = content,
                startTime = startTime!!,
                maxParticipants = maxParticipants,
                currentParticipants = currentParticipants,
                status = status,
            )
    }
}
