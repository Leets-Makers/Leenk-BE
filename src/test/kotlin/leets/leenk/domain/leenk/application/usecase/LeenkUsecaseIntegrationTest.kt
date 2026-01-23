package leets.leenk.domain.leenk.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.leenk.application.exception.AlreadyParticipatedException
import leets.leenk.domain.leenk.application.exception.LeenkNotRecruitingException
import leets.leenk.domain.leenk.application.exception.MaxParticipantsExceededException
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository
import leets.leenk.domain.leenk.domain.repository.LeenkRepository
import leets.leenk.domain.leenk.domain.repository.LocationRepository
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.repository.UserRepository
import leets.leenk.domain.user.test.fixture.UserTestFixture
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

/**
 * LeenkUsecase 통합 테스트
 *
 * 검증 대상:
 * - participateLeenk: 링크 참여 시 비즈니스 규칙 검증 (상태, 중복, 인원), currentParticipants 증가
 * - closeLeenk: 링크 마감 시 권한/상태 검증,상태 변경
 * - finishLeenk: 링크 완료 시 권한/상태 검증, 상태 변경
 * - kickParticipant: 참여자 강퇴 시 권한/자기자신 검증, currentParticipants 감소
 * - leaveLeenk: 링크 나가기 시 호스트 검증, currentParticipants 감소
 */
@SpringBootTest
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeenkUsecaseIntegrationTest(
    private val leenkUsecase: LeenkUsecase,
    private val userRepository: UserRepository,
    private val locationRepository : LocationRepository,
    private val leenkRepository: LeenkRepository,
    private val leenkParticipantsRepository: LeenkParticipantsRepository
) : BehaviorSpec({

    afterEach {
        leenkParticipantsRepository.deleteAll()
        leenkRepository.deleteAll()
        locationRepository.deleteAll()
        userRepository.deleteAll()
    }

    // 헬퍼
    fun persistUser(id: Long, name: String = "테스트유저"): User =
        userRepository.save(
            UserTestFixture.createUser(id = id, name = name)
        )

    fun persistLocation() =
        locationRepository.save(
            LocationTestFixture.createLocation(id = null, "서울")
        )

    fun persistLeenk(
        author: User,
        status: LeenkStatus = LeenkStatus.RECRUITING,
        currentParticipants: Long = 0,
        maxParticipants: Long = 2
    ) = leenkRepository.save(
        LeenkTestFixture.createLeenk(
            id = null,
            author = author,
            location = persistLocation(),
            status = status,
            currentParticipants = currentParticipants,
            maxParticipants = maxParticipants
        )
    )

    Given("participateLeenk - 링크 참여") {

        When("일반 사용자가 모집 중인 링크에 참여하면") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(author = host)
            val initialCount = leenk.currentParticipants

            Then("정상적으로 참여되고 currentParticipants가 1 증가한다") {
                leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

                val updated = leenkRepository.findById(leenk.id!!).get()
                updated.currentParticipants shouldBe initialCount + 1
            }
        }

        When("모집 중이 아닌 링크(CLOSED)에 참여 시도하면") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(
                author = host,
                status = LeenkStatus.CLOSED
            )

            Then("LeenkNotRecruitingException이 발생한다") {
                shouldThrow<LeenkNotRecruitingException> {
                    leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                }
            }
        }

        When("이미 참여한 사용자가 다시 참여 시도하면") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(author = host)

            leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

            Then("AlreadyParticipatedException이 발생한다") {
                shouldThrow<AlreadyParticipatedException> {
                    leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                }
            }
        }

        When("최대 인원이 이미 찬 링크에 참여 시도하면") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(
                author = host,
                currentParticipants = 2,
                maxParticipants = 2
            )

            Then("MaxParticipantsExceededException이 발생한다") {
                shouldThrow<MaxParticipantsExceededException> {
                    leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                }
            }
        }

        When("최대 인원 직전 상태에서 참여하면") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(
                author = host,
                currentParticipants = 1,
                maxParticipants = 2
            )

            Then("정상적으로 참여되어 최대 인원이 된다") {
                leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

                val updated = leenkRepository.findById(leenk.id!!).get()
                updated.currentParticipants shouldBe updated.maxParticipants
            }
        }
    }
})
