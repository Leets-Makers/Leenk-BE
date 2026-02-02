package leets.leenk.domain.leenk.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.entity.BaseEntity
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "leenk_participants",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "leenk_id"]),
    ],
)
@EntityListeners(AuditingEntityListener::class)
class LeenkParticipants(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leenk_participant_id")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leenk_id", nullable = false, updatable = false)
    val leenk: Leenk,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val participant: User,
    @Column(nullable = false)
    @CreatedDate
    var joinedAt: LocalDateTime? = null,
) : BaseEntity() {
    companion object {
        @JvmStatic
        fun builder(): LeenkParticipantsBuilder = LeenkParticipantsBuilder()
    }

    class LeenkParticipantsBuilder {
        private var id: Long? = null
        private var leenk: Leenk? = null
        private var participant: User? = null
        private var joinedAt: LocalDateTime? = null

        fun id(id: Long?): LeenkParticipantsBuilder {
            this.id = id
            return this
        }

        fun leenk(leenk: Leenk): LeenkParticipantsBuilder {
            this.leenk = leenk
            return this
        }

        fun participant(participant: User): LeenkParticipantsBuilder {
            this.participant = participant
            return this
        }

        fun joinedAt(joinedAt: LocalDateTime?): LeenkParticipantsBuilder {
            this.joinedAt = joinedAt
            return this
        }

        fun build(): LeenkParticipants =
            LeenkParticipants(
                id = id,
                leenk = leenk!!,
                participant = participant!!,
                joinedAt = joinedAt,
            )
    }
}
