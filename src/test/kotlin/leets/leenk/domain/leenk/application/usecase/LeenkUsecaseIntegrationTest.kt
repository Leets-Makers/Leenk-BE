package leets.leenk.domain.leenk.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import leets.leenk.config.MongoTestConfig
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.leenk.application.exception.*
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
@Import(MysqlTestConfig::class, MongoTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeenkUsecaseIntegrationTest(
    private val leenkUsecase: LeenkUsecase,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val leenkRepository: LeenkRepository,
    private val leenkParticipantsRepository: LeenkParticipantsRepository,
) : DescribeSpec() {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    private lateinit var host: User
    private lateinit var otherUser: User

    private fun persistUser(
        id: Long,
        name: String = "테스트유저",
    ): User = userRepository.save(UserTestFixture.createUser(id = id, name = name))

    private fun persistLocation() = locationRepository.save(LocationTestFixture.createLocation(id = null, "서울"))

    private fun persistLeenk(
        author: User = host,
        status: LeenkStatus = LeenkStatus.RECRUITING,
        currentParticipants: Long = 1,
        maxParticipants: Long = 2,
    ) = leenkRepository.save(
        LeenkTestFixture.createLeenk(
            id = null,
            author = author,
            location = persistLocation(),
            status = status,
            currentParticipants = currentParticipants,
            maxParticipants = maxParticipants,
        ),
    )

    init {
        beforeEach {
            host = persistUser(id = 1L, name = "호스트")
            otherUser = persistUser(id = 2L, name = "다른 사용자")
        }

        afterEach {
            leenkParticipantsRepository.deleteAll()
            leenkRepository.deleteAll()
            locationRepository.deleteAll()
            userRepository.deleteAll()
        }

        describe("participateLeenk - 링크 참여") {
            context("모집 중인 링크에 일반 사용자가 참여하는 경우") {
                it("참여가 완료되고 currentParticipants가 1 증가한다") {
                    val leenk = persistLeenk()
                    val initialCount = leenk.currentParticipants

                    leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount + 1
                }
            }

            context("CLOSED 상태의 링크에 참여하는 경우") {
                it("LeenkNotRecruitingException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)
                    }
                }
            }

            context("이미 참여한 사용자가 다시 참여하는 경우") {
                it("AlreadyParticipatedException이 발생한다") {
                    val leenk = persistLeenk()
                    leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)

                    shouldThrow<AlreadyParticipatedException> {
                        leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)
                    }
                }
            }

            context("최대 인원이 이미 찬 링크에 참여하는 경우") {
                it("MaxParticipantsExceededException이 발생한다") {
                    val leenk = persistLeenk(currentParticipants = 2, maxParticipants = 2)

                    shouldThrow<MaxParticipantsExceededException> {
                        leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)
                    }
                }
            }

            context("최대 인원 직전 상태의 링크에 참여하는 경우") {
                it("참여가 완료되어 최대 인원이 된다") {
                    val leenk = persistLeenk(currentParticipants = 1, maxParticipants = 2)

                    leenkUsecase.participateLeenk(otherUser.id!!, leenk.id!!)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe updated.maxParticipants
                }
            }
        }

        describe("closeLeenk - 링크 마감") {
            context("호스트가 모집 중인 링크를 마감하는 경우") {
                it("링크 상태가 CLOSED로 변경된다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    leenkUsecase.closeLeenk(host.id!!, leenk.id!!)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.CLOSED
                }
            }

            context("호스트가 아닌 사용자가 링크를 마감하는 경우") {
                it("NotLeenkOwnerException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.closeLeenk(otherUser.id!!, leenk.id!!)
                    }
                }
            }

            context("이미 마감된 링크를 다시 마감하는 경우") {
                it("LeenkAlreadyClosedException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    shouldThrow<LeenkAlreadyClosedException> {
                        leenkUsecase.closeLeenk(host.id!!, leenk.id!!)
                    }
                }
            }

            context("완료된 상태의 링크를 마감하는 경우") {
                it("LeenkAlreadyClosedException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.FINISHED)

                    shouldThrow<LeenkAlreadyClosedException> {
                        leenkUsecase.closeLeenk(host.id!!, leenk.id!!)
                    }
                }
            }
        }

        describe("finishLeenk - 링크 완료") {
            context("호스트가 마감된 링크를 완료 처리하는 경우") {
                it("링크 상태가 FINISHED로 변경된다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    leenkUsecase.finishLeenk(host.id, leenk.id)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.FINISHED
                }
            }

            context("호스트가 아닌 사용자가 링크를 완료하는 경우") {
                it("NotLeenkOwnerException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.finishLeenk(otherUser.id, leenk.id)
                    }
                }
            }

            context("모집 중인 링크를 완료하는 경우") {
                it("RECRUITING에서 FINISHED로 직접 전환된다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    leenkUsecase.finishLeenk(host.id, leenk.id)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.FINISHED
                }
            }

            context("이미 완료된 링크를 다시 완료하는 경우") {
                it("LeenkAlreadyFinishedException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.FINISHED)

                    shouldThrow<LeenkAlreadyFinishedException> {
                        leenkUsecase.finishLeenk(host.id, leenk.id)
                    }
                }
            }
        }

        describe("kickParticipant - 참여자 강퇴") {
            context("호스트가 일반 참여자를 강퇴하는 경우") {
                it("참여자가 제거되고 currentParticipants가 1 감소한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)
                    leenkUsecase.participateLeenk(otherUser.id, leenk.id)
                    val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
                    val initialCount = updatedAfterParticipate.currentParticipants

                    leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }

            context("호스트가 아닌 사용자가 참여자를 강퇴하는 경우") {
                it("NotLeenkOwnerException이 발생한다") {
                    val participant = persistUser(3L, "참여자")
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)
                    leenkUsecase.participateLeenk(participant.id, leenk.id)

                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.kickParticipant(otherUser.id, leenk.id, participant.id)
                    }
                }
            }

            context("호스트가 자기 자신을 강퇴하는 경우") {
                it("CannotKickSelfException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    shouldThrow<CannotKickSelfException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, host.id)
                    }
                }
            }

            context("링크에 참여하지 않은 사용자를 강퇴하는 경우") {
                it("LeenkParticipantNotFoundException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    shouldThrow<LeenkParticipantNotFoundException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)
                    }
                }
            }

            context("마감된 링크에서 참여자를 강퇴하는 경우") {
                it("LeenkNotRecruitingException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)
                    }
                }
            }

            context("완료된 링크에서 참여자를 강퇴하는 경우") {
                it("LeenkNotRecruitingException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.FINISHED)

                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)
                    }
                }
            }
        }

        describe("leaveLeenk - 링크 나가기") {
            context("일반 참여자가 링크를 나가는 경우") {
                it("정상적으로 나가지고 currentParticipants가 1 감소한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)
                    leenkUsecase.participateLeenk(otherUser.id, leenk.id)
                    val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
                    val initialCount = updatedAfterParticipate.currentParticipants

                    leenkUsecase.leaveLeenk(otherUser.id, leenk.id)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }

            context("호스트가 링크를 나가는 경우") {
                it("CannotLeaveAsHostException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    shouldThrow<CannotLeaveAsHostException> {
                        leenkUsecase.leaveLeenk(host.id, leenk.id)
                    }
                }
            }

            context("참여하지 않은 사용자가 링크를 나가는 경우") {
                it("LeenkParticipantNotFoundException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)

                    shouldThrow<LeenkParticipantNotFoundException> {
                        leenkUsecase.leaveLeenk(otherUser.id, leenk.id)
                    }
                }
            }

            context("마감된 링크에서 나가는 경우") {
                it("LeenkNotRecruitingException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.CLOSED)

                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.leaveLeenk(otherUser.id, leenk.id)
                    }
                }
            }

            context("완료된 링크에서 나가는 경우") {
                it("LeenkNotRecruitingException이 발생한다") {
                    val leenk = persistLeenk(status = LeenkStatus.FINISHED)

                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.leaveLeenk(otherUser.id, leenk.id)
                    }
                }
            }

            context("마지막 일반 참여자가 링크를 나가는 경우") {
                it("호스트만 남고 currentParticipants가 1 감소한다") {
                    val leenk = persistLeenk(status = LeenkStatus.RECRUITING)
                    leenkUsecase.participateLeenk(otherUser.id, leenk.id)
                    val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
                    val initialCount = updatedAfterParticipate.currentParticipants

                    leenkUsecase.leaveLeenk(otherUser.id, leenk.id)

                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }
        }
    }
}
