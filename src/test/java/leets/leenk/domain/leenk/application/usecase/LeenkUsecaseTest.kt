package leets.leenk.domain.leenk.application.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.leenk.application.exception.AlreadyParticipatedException
import leets.leenk.domain.leenk.application.exception.LeenkNotRecruitingException
import leets.leenk.domain.leenk.application.exception.MaxParticipantsExceededException
import leets.leenk.domain.leenk.application.mapper.LeenkParticipantsMapper
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.leenk.domain.entity.Location
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.domain.service.LeenkGetService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsGetService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsSaveService
import leets.leenk.domain.leenk.test.fixture.LeenkParticipantsTestFixture
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.domain.user.test.fixture.UserTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class LeenkUsecaseTest {

    private val userGetService: UserGetService = mockk()
    private val leenkGetService: LeenkGetService = mockk()
    private val leenkParticipantsGetService: LeenkParticipantsGetService = mockk()
    private val leenkParticipantsSaveService: LeenkParticipantsSaveService = mockk()
    private val participantsMapper: LeenkParticipantsMapper = mockk()
    private val leenkNotificationUsecase: LeenkNotificationUsecase = mockk(relaxed = true)

    private lateinit var leenkUsecase: LeenkUsecase

    private lateinit var user: User
    private lateinit var recruitingLeenk: Leenk
    private lateinit var participant: LeenkParticipants
    private lateinit var location: Location

    @BeforeEach
    fun setUp() {
        leenkUsecase = LeenkUsecase(
            mockk(),
            mockk(),
            leenkGetService,
            mockk(),
            mockk(),
            leenkParticipantsSaveService,
            leenkParticipantsGetService,
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            userGetService,
            mockk(),
            mockk(),
            mockk(),
            participantsMapper,
            mockk(),
            mockk(),
            leenkNotificationUsecase
        )

        user = UserTestFixture.createUser(id = 1L, name = "김철수")
        location = LocationTestFixture.createLocation(id = 1L, placeName = "테스트 장소")
        recruitingLeenk = LeenkTestFixture.createLeenk(
            id = 1L,
            author = user,
            location = location,
            status = LeenkStatus.RECRUITING,
            currentParticipants = 2L,
            maxParticipants = 10L
        )
        participant = LeenkParticipantsTestFixture.createParticipant(
            leenk = recruitingLeenk,
            participant = user
        )
    }

    @Nested
    @DisplayName("participateLeenk 관련 테스트")
    inner class ParticipateLeenkTests {
        @Test
        @DisplayName("모집 중인 링크에 정상적으로 참여한다")
        fun participateLeenkSuccess() {
            // given
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns recruitingLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(recruitingLeenk, user) } returns false
            every { participantsMapper.toParticipants(recruitingLeenk, user, any()) } returns participant
            every { leenkParticipantsSaveService.save(any()) } returns participant

            val initialParticipants = recruitingLeenk.currentParticipants

            // when
            leenkUsecase.participateLeenk(1L, 1L)

            // then
            verify(exactly = 1) { leenkParticipantsSaveService.save(participant) }
            verify(exactly = 1) { leenkNotificationUsecase.saveNewLeenkParticipantNotification(recruitingLeenk, user) }
            assertThat(recruitingLeenk.currentParticipants).isEqualTo(initialParticipants + 1)
        }

        @Test
        @DisplayName("모집 중이 아닌 링크에 참여 시 예외가 발생한다")
        fun participateLeenkNotRecruitingThrowsException() {
            // given
            val location = LocationTestFixture.createLocation(id = 2L)
            val closedLeenk = LeenkTestFixture.createLeenk(
                id = 1L,
                author = user,
                location = location,
                status = LeenkStatus.CLOSED,
                currentParticipants = 5L,
                maxParticipants = 10L
            )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns closedLeenk

            // when & then
            assertThatThrownBy { leenkUsecase.participateLeenk(1L, 1L) }
                .isInstanceOf(LeenkNotRecruitingException::class.java)

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { leenkNotificationUsecase.saveNewLeenkParticipantNotification(any(), any()) }
        }

        @Test
        @DisplayName("이미 참여한 사용자가 다시 참여 시 예외가 발생한다")
        fun participateLeenkAlreadyParticipatedThrowsException() {
            // given
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns recruitingLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(recruitingLeenk, user) } returns true

            // when & then
            assertThatThrownBy { leenkUsecase.participateLeenk(1L, 1L) }
                .isInstanceOf(AlreadyParticipatedException::class.java)

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { leenkNotificationUsecase.saveNewLeenkParticipantNotification(any(), any()) }
        }

        @Test
        @DisplayName("최대 참여 인원을 초과하면 예외가 발생한다")
        fun participateLeenkMaxParticipantsExceededThrowsException() {
            // given
            val location = LocationTestFixture.createLocation(id = 3L)
            val fullLeenk = LeenkTestFixture.createLeenk(
                id = 1L,
                author = user,
                location = location,
                status = LeenkStatus.RECRUITING,
                currentParticipants = 10L,
                maxParticipants = 10L
            )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns fullLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(fullLeenk, user) } returns false

            // when & then
            assertThatThrownBy { leenkUsecase.participateLeenk(1L, 1L) }
                .isInstanceOf(MaxParticipantsExceededException::class.java)

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { leenkNotificationUsecase.saveNewLeenkParticipantNotification(any(), any()) }
        }

        @Test
        @DisplayName("최대 참여 인원 직전에 참여하면 정상적으로 처리된다")
        fun participateLeenkLastSlotSuccess() {
            // given
            val location = LocationTestFixture.createLocation(id = 4L)
            val almostFullLeenk = LeenkTestFixture.createLeenk(
                id = 1L,
                author = user,
                location = location,
                status = LeenkStatus.RECRUITING,
                currentParticipants = 9L,
                maxParticipants = 10L
            )
            val lastParticipant = LeenkParticipantsTestFixture.createParticipant(
                leenk = almostFullLeenk,
                participant = user
            )

            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns almostFullLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(almostFullLeenk, user) } returns false
            every { participantsMapper.toParticipants(almostFullLeenk, user, any()) } returns lastParticipant
            every { leenkParticipantsSaveService.save(any()) } returns lastParticipant

            // when
            leenkUsecase.participateLeenk(1L, 1L)

            // then
            verify(exactly = 1) { leenkParticipantsSaveService.save(lastParticipant) }
            verify(exactly = 1) { leenkNotificationUsecase.saveNewLeenkParticipantNotification(almostFullLeenk, user) }
            assertThat(almostFullLeenk.currentParticipants).isEqualTo(10L)
        }
    }
}