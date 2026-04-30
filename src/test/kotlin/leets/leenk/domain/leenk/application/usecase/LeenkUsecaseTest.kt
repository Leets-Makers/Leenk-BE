package leets.leenk.domain.leenk.application.usecase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.leenk.application.exception.*
import leets.leenk.domain.leenk.application.mapper.LeenkMapper
import leets.leenk.domain.leenk.application.mapper.LeenkParticipantsMapper
import leets.leenk.domain.leenk.application.mapper.LocationMapper
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.leenk.domain.entity.Location
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.domain.service.*
import leets.leenk.domain.leenk.test.fixture.LeenkParticipantsTestFixture
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.media.application.mapper.MediaMapper
import leets.leenk.domain.media.domain.service.MediaDeleteService
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaSaveService
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.NotionDatabaseService
import leets.leenk.domain.user.domain.service.SlackWebhookService
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.domain.user.test.fixture.UserTestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.context.ApplicationEventPublisher

class LeenkUsecaseTest {
    private val locationSaveService: LocationSaveService = mockk()
    private val leenkSaveService: LeenkSaveService = mockk()
    private val leenkGetService: LeenkGetService = mockk()
    private val leenkUpdateService: LeenkUpdateService = mockk()
    private val leenkDeleteService: LeenkDeleteService = mockk()

    private val leenkParticipantsSaveService: LeenkParticipantsSaveService = mockk()
    private val leenkParticipantsGetService: LeenkParticipantsGetService = mockk()
    private val leenkParticipantsDeleteService: LeenkParticipantsDeleteService = mockk()

    private val mediaSaveService: MediaSaveService = mockk()
    private val mediaGetService: MediaGetService = mockk()
    private val mediaDeleteService: MediaDeleteService = mockk()

    private val userGetService: UserGetService = mockk()
    private val slackWebhookService: SlackWebhookService = mockk()
    private val notionDatabaseService: NotionDatabaseService = mockk()

    private val leenkMapper: LeenkMapper = mockk()
    private val participantsMapper: LeenkParticipantsMapper = mockk()
    private val locationMapper: LocationMapper = mockk()
    private val mediaMapper: MediaMapper = mockk()

    private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
    private lateinit var leenkUsecase: LeenkUsecase
    private lateinit var user: User
    private lateinit var recruitingLeenk: Leenk
    private lateinit var participant: LeenkParticipants
    private lateinit var location: Location

    private fun createLeenkUsecase(
        locationSaveService: LocationSaveService,
        leenkSaveService: LeenkSaveService,
        leenkGetService: LeenkGetService,
        leenkUpdateService: LeenkUpdateService,
        leenkDeleteService: LeenkDeleteService,
        leenkParticipantsSaveService: LeenkParticipantsSaveService,
        leenkParticipantsGetService: LeenkParticipantsGetService,
        leenkParticipantsDeleteService: LeenkParticipantsDeleteService,
        mediaSaveService: MediaSaveService,
        mediaGetService: MediaGetService,
        mediaDeleteService: MediaDeleteService,
        userGetService: UserGetService,
        slackWebhookService: SlackWebhookService,
        notionDatabaseService: NotionDatabaseService,
        leenkMapper: LeenkMapper,
        participantsMapper: LeenkParticipantsMapper,
        locationMapper: LocationMapper,
        mediaMapper: MediaMapper,
        eventPublisher: ApplicationEventPublisher,
    ): LeenkUsecase =
        LeenkUsecase(
            locationSaveService,
            leenkSaveService,
            leenkGetService,
            leenkUpdateService,
            leenkDeleteService,
            leenkParticipantsSaveService,
            leenkParticipantsGetService,
            leenkParticipantsDeleteService,
            mediaSaveService,
            mediaGetService,
            mediaDeleteService,
            userGetService,
            slackWebhookService,
            notionDatabaseService,
            leenkMapper,
            participantsMapper,
            locationMapper,
            mediaMapper,
            eventPublisher,
        )

    @BeforeEach
    fun setUp() {
        leenkUsecase =
            createLeenkUsecase(
                locationSaveService = locationSaveService,
                leenkSaveService = leenkSaveService,
                leenkGetService = leenkGetService,
                leenkUpdateService = leenkUpdateService,
                leenkDeleteService = leenkDeleteService,
                leenkParticipantsSaveService = leenkParticipantsSaveService,
                leenkParticipantsGetService = leenkParticipantsGetService,
                leenkParticipantsDeleteService = leenkParticipantsDeleteService,
                mediaSaveService = mediaSaveService,
                mediaGetService = mediaGetService,
                mediaDeleteService = mediaDeleteService,
                userGetService = userGetService,
                slackWebhookService = slackWebhookService,
                notionDatabaseService = notionDatabaseService,
                leenkMapper = leenkMapper,
                participantsMapper = participantsMapper,
                locationMapper = locationMapper,
                mediaMapper = mediaMapper,
                eventPublisher = eventPublisher,
            )

        user = UserTestFixture.createUser(id = 1L)
        location = LocationTestFixture.createLocation(id = 1L)
        recruitingLeenk =
            LeenkTestFixture.createLeenk(
                id = 1L,
                author = user,
                location = location,
                status = LeenkStatus.RECRUITING,
                currentParticipants = 2L,
                maxParticipants = 10L,
            )
        participant =
            LeenkParticipantsTestFixture.createParticipant(
                leenk = recruitingLeenk,
                participant = user,
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
            verify(exactly = 1) { eventPublisher.publishEvent(any()) }
            assertThat(recruitingLeenk.currentParticipants).isEqualTo(initialParticipants + 1)
        }

        @Test
        @DisplayName("모집 중이 아닌 링크에 참여 시 예외가 발생한다")
        fun participateLeenkNotRecruitingThrowsException() {
            // given
            val closedLeenk =
                LeenkTestFixture.createClosedLeenk(
                    id = 1L,
                    author = user,
                    location = location,
                )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns closedLeenk

            // when & then
            assertThrows<LeenkNotRecruitingException> {
                leenkUsecase.participateLeenk(1L, 1L)
            }

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("이미 참여한 사용자가 다시 참여 시 예외가 발생한다")
        fun participateLeenkAlreadyParticipatedThrowsException() {
            // given
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns recruitingLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(recruitingLeenk, user) } returns true

            // when & then
            assertThrows<AlreadyParticipatedException> {
                leenkUsecase.participateLeenk(1L, 1L)
            }

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("최대 참여 인원을 초과하면 예외가 발생한다")
        fun participateLeenkMaxParticipantsExceededThrowsException() {
            // given
            val fullLeenk =
                LeenkTestFixture.createFullLeenk(
                    id = 1L,
                    author = user,
                    location = location,
                )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns fullLeenk
            every { leenkParticipantsGetService.existsByLeenkAndParticipant(fullLeenk, user) } returns false

            // when & then
            assertThrows<MaxParticipantsExceededException> {
                leenkUsecase.participateLeenk(1L, 1L)
            }

            verify(exactly = 0) { leenkParticipantsSaveService.save(any()) }
            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("최대 참여 인원 직전에 참여하면 정상적으로 처리된다")
        fun participateLeenkLastSlotSuccess() {
            // given
            val almostFullLeenk =
                LeenkTestFixture.createAlmostFullLeenk(
                    id = 1L,
                    author = user,
                    location = location,
                )

            val lastParticipant =
                LeenkParticipantsTestFixture.createParticipant(
                    leenk = almostFullLeenk,
                    participant = user,
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
            verify(exactly = 1) { eventPublisher.publishEvent(any()) }
            assertThat(almostFullLeenk.currentParticipants).isEqualTo(almostFullLeenk.maxParticipants)
        }
    }

    @Nested
    @DisplayName("closeLeenk 관련 테스트")
    inner class CloseLeenkTests {
        @Test
        @DisplayName("링크 작성자가 모집 중인 링크를 정상적으로 마감한다")
        fun closeLeenkSuccess() {
            // given
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns recruitingLeenk

            // when
            leenkUsecase.closeLeenk(1L, 1L)

            // then
            assertThat(recruitingLeenk.status).isEqualTo(LeenkStatus.CLOSED)
            verify(exactly = 1) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("링크 작성자가 아닌 사용자가 마감 시도 시 예외가 발생한다")
        fun closeLeenkNotOwnerThrowsException() {
            // given
            val otherUser = UserTestFixture.createUser(id = 2L, name = "김영희")
            every { userGetService.findById(2L) } returns otherUser
            every { leenkGetService.findById(1L) } returns recruitingLeenk

            // when & then
            assertThrows<NotLeenkOwnerException> {
                leenkUsecase.closeLeenk(2L, 1L)
            }

            assertThat(recruitingLeenk.status).isEqualTo(LeenkStatus.RECRUITING)
            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("이미 마감된 링크를 다시 마감 시 예외가 발생한다")
        fun closeLeenkAlreadyClosedThrowsException() {
            // given
            val closedLeenk =
                LeenkTestFixture.createClosedLeenk(
                    id = 1L,
                    author = user,
                    location = location,
                )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns closedLeenk

            // when & then
            assertThrows<LeenkAlreadyClosedException> {
                leenkUsecase.closeLeenk(1L, 1L)
            }

            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }

        @Test
        @DisplayName("완료된 상태의 링크를 마감 시 예외가 발생한다")
        fun closeLeenkCompletedStatusThrowsException() {
            // given
            val completedLeenk =
                LeenkTestFixture.createFinishedLeenk(
                    id = 1L,
                    author = user,
                    location = location,
                )
            every { userGetService.findById(1L) } returns user
            every { leenkGetService.findById(1L) } returns completedLeenk

            // when & then
            assertThrows<LeenkAlreadyClosedException> {
                leenkUsecase.closeLeenk(1L, 1L)
            }

            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }
    }
}
