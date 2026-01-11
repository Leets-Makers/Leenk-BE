package leets.leenk.domain.notification.domain.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import leets.leenk.domain.notification.test.fixture.NotificationFixture
import leets.leenk.domain.user.test.fixture.UserFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.impl.annotations.InjectMockKs
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class NotificationMarkReadServiceTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository
    @InjectMockKs
    private lateinit var notificationMarkReadService: NotificationMarkReadService;

    @Test
    fun markReadNotification() {
        // given
        val user = UserFixture.basicUser();
        val notification = NotificationFixture.basicNotification();

        every {
            notificationRepository.findById(notification.id)
        } returns Optional.of(notification)

        every {
            notificationRepository.save(notification)
        } returns notification

        // when
        notificationMarkReadService.markReadNotification(user, notification.id);

        // then
        assert(notification.isRead)
        verify(exactly = 1) {
            notificationRepository.save(notification)
        }
    }
}
