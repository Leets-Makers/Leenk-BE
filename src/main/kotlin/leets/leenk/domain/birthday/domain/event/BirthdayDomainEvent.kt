package leets.leenk.domain.birthday.domain.event

sealed class BirthdayDomainEvent {
    data class TodayBirthday(
        val birthdayUsers: List<BirthdayUserInfo>,
        val receiverIds: List<Long>,
    ) : BirthdayDomainEvent()

    data class BirthdayUserInfo(
        val id: Long,
        val name: String,
    )

    data class LetterSent(
        val letterId: Long,
        val senderName: String,
        val receiverId: Long,
    ) : BirthdayDomainEvent()
}
