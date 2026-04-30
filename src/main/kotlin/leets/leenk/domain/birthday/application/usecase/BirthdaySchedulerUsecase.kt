package leets.leenk.domain.birthday.application.usecase

import leets.leenk.domain.birthday.domain.event.BirthdayDomainEvent
import leets.leenk.domain.birthday.domain.service.BirthdayGetService
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BirthdaySchedulerUsecase(
    private val birthdayGetService: BirthdayGetService,
    private val userSettingGetService: UserSettingGetService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun announceAndCelebrateBirthdays(today: LocalDate) {
        val birthdayUsers = birthdayGetService.findTodayBirthdayUsers(today)

        if (birthdayUsers.isEmpty()) return

        val receiverIds = userSettingGetService.getUsersToNotifyBirthday().mapNotNull { it.id }

        eventPublisher.publishEvent(
            BirthdayDomainEvent.TodayBirthday(
                birthdayUsers = birthdayUsers.map {
                    BirthdayDomainEvent.BirthdayUserInfo(it.id!!, it.name)
                },
                receiverIds = receiverIds,
            ),
        )
    }
}
