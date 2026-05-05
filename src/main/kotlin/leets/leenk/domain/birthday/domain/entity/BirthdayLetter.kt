package leets.leenk.domain.birthday.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.entity.BaseEntity

@Entity
@Table(name = "birthday_letters")
class BirthdayLetter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    val id: Long? = null,
    @Column(nullable = false, length = 40)
    val message: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    val sender: User,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false, updatable = false)
    val receiver: User,
) : BaseEntity() {
    val requireId: Long
        get() = checkNotNull(id) { "영속화되지 않은 BirthdayLetter 엔티티입니다" }
}
