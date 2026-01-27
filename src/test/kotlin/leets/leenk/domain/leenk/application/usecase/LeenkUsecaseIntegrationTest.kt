package leets.leenk.domain.leenk.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
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
) : BehaviorSpec({

        afterEach {
            leenkParticipantsRepository.deleteAll()
            leenkRepository.deleteAll()
            locationRepository.deleteAll()
            userRepository.deleteAll()
        }

        // 헬퍼
        fun persistUser(
            id: Long,
            name: String = "테스트유저",
        ): User = userRepository.save(UserTestFixture.createUser(id = id, name = name))

        fun persistLocation() = locationRepository.save(LocationTestFixture.createLocation(id = null, "서울"))

        fun persistLeenk(
            author: User,
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

        // participateLeenk 링크 참여
        Given("모집 중인 링크에 일반 사용자가 참여하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(author = host)
            val initialCount = leenk.currentParticipants

            When("링크에 참여하면") {
                leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

                Then("참여가 완료되고 currentParticipants가 1 증가한다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount + 1
                }
            }
        }

        Given("CLOSED 상태의 링크에 참여하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk =
                persistLeenk(
                    author = host,
                    status = LeenkStatus.CLOSED,
                )

            When("링크에 참여하면") {
                Then("LeenkNotRecruitingException이 발생한다") {
                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                    }
                }
            }
        }

        Given("이미 참여한 사용자가 다시 참여하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk = persistLeenk(author = host)

            leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

            When("다시 참여하면") {
                Then("AlreadyParticipatedException이 발생한다") {
                    shouldThrow<AlreadyParticipatedException> {
                        leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                    }
                }
            }
        }

        Given("최대 인원이 이미 찬 링크에 참여하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk =
                persistLeenk(
                    author = host,
                    currentParticipants = 2,
                    maxParticipants = 2,
                )

            When("링크에 참여하면") {
                Then("MaxParticipantsExceededException이 발생한다") {
                    shouldThrow<MaxParticipantsExceededException> {
                        leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)
                    }
                }
            }
        }

        Given("최대 인원 직전 상태의 링크에 참여하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val participant = persistUser(id = 2L, name = "참여자")
            val leenk =
                persistLeenk(
                    author = host,
                    currentParticipants = 1,
                    maxParticipants = 2,
                )

            When("링크에 참여하면") {
                leenkUsecase.participateLeenk(participant.id!!, leenk.id!!)

                Then("참여가 완료되어 최대 인원이 된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe updated.maxParticipants
                }
            }
        }

        // closeLeenk 링크 마감
        Given("호스트가 모집 중인 링크를 마감하는 경우") {
            val host = persistUser(1L, "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("링크를 마감하면") {
                leenkUsecase.closeLeenk(host.id!!, leenk.id!!)

                Then("링크 상태가 CLOSED로 변경된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.CLOSED
                }
            }
        }

        Given("호스트가 아닌 사용자가 링크를 마감하는 경우") {
            val host = persistUser(1L, "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("링크를 마감하면") {
                Then("NotLeenkOwnerException이 발생한다") {
                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.closeLeenk(otherUser.id!!, leenk.id!!)
                    }
                }
            }
        }

        Given("이미 마감된 링크를 다시 마감하는 경우") {
            val host = persistUser(1L, "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.CLOSED)

            When("링크를 마감하면") {
                Then("LeenkAlreadyClosedException이 발생한다") {
                    shouldThrow<LeenkAlreadyClosedException> {
                        leenkUsecase.closeLeenk(host.id!!, leenk.id!!)
                    }
                }
            }
        }

        Given("완료된 상태의 링크를 마감하는 경우") {
            val host = persistUser(1L, "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.FINISHED)

            When("링크를 마감하면") {
                Then("LeenkAlreadyClosedException이 발생한다") {
                    shouldThrow<LeenkAlreadyClosedException> {
                        leenkUsecase.closeLeenk(host.id!!, leenk.id!!)
                    }
                }
            }
        }

        // finishLeenk 링크 완료
        Given("호스트가 마감된 링크를 완료 처리하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.CLOSED)

            When("링크를 완료하면") {
                leenkUsecase.finishLeenk(host.id, leenk.id)

                Then("링크 상태가 FINISHED로 변경된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.FINISHED
                }
            }
        }

        Given("호스트가 아닌 사용자가 링크를 완료하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.CLOSED)

            When("링크를 완료하면") {
                Then("NotLeenkOwnerException이 발생한다") {
                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.finishLeenk(otherUser.id, leenk.id)
                    }
                }
            }
        }

        Given("모집 중인 링크를 완료하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("링크를 완료하면") {
                leenkUsecase.finishLeenk(host.id, leenk.id)

                Then("RECRUITING에서 FINISHED로 직접 전환된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.status shouldBe LeenkStatus.FINISHED
                }
            }
        }

        Given("이미 완료된 링크를 다시 완료하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.FINISHED)

            When("링크를 완료하면") {
                Then("LeenkAlreadyFinishedException이 발생한다") {
                    shouldThrow<LeenkAlreadyFinishedException> {
                        leenkUsecase.finishLeenk(host.id, leenk.id)
                    }
                }
            }
        }

        // kickParticipant 참여자 강퇴
        Given("호스트가 일반 참여자를 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            // 참여자 추가
            leenkUsecase.participateLeenk(otherUser.id, leenk.id)

            // DB에서 최신 currentParticipants 가져오기
            val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
            val initialCount = updatedAfterParticipate.currentParticipants

            When("참여자를 강퇴하면") {
                leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)

                Then("참여자가 제거되고 currentParticipants가 1 감소하며 알림이 전송된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }
        }

        Given("호스트가 아닌 사용자가 참여자를 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val participant = persistUser(3L, "참여자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            // 참여자 추가
            leenkUsecase.participateLeenk(participant.id, leenk.id)

            When("참여자를 강퇴하면") {
                Then("NotLeenkOwnerException이 발생한다") {
                    shouldThrow<NotLeenkOwnerException> {
                        leenkUsecase.kickParticipant(otherUser.id, leenk.id, participant.id)
                    }
                }
            }
        }

        Given("호스트가 자기 자신을 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("자신을 강퇴하면") {
                Then("CannotKickSelfException이 발생한다") {
                    shouldThrow<CannotKickSelfException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, host.id)
                    }
                }
            }
        }

        Given("링크에 참여하지 않은 사용자를 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val nonParticipant = persistUser(2L, "미참여자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("미참여자를 강퇴하면") {
                Then("LeenkParticipantNotFoundException이 발생한다") {
                    shouldThrow<LeenkParticipantNotFoundException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, nonParticipant.id)
                    }
                }
            }
        }

        Given("마감된 링크에서 참여자를 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.CLOSED)

            When("참여자를 강퇴하면") {
                Then("LeenkNotRecruitingException이 발생한다") {
                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)
                    }
                }
            }
        }

        Given("완료된 링크에서 참여자를 강퇴하는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.FINISHED)

            When("참여자를 강퇴하면") {
                Then("LeenkNotRecruitingException이 발생한다") {
                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.kickParticipant(host.id, leenk.id, otherUser.id)
                    }
                }
            }
        }

        // leaveLeenk 링크 나가기
        Given("일반 참여자가 링크를 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            // 참여자 추가
            leenkUsecase.participateLeenk(otherUser.id, leenk.id)

            // DB에서 최신 currentParticipants 가져오기
            val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
            val initialCount = updatedAfterParticipate.currentParticipants

            When("링크를 나가면") {
                leenkUsecase.leaveLeenk(otherUser.id, leenk.id)

                Then("정상적으로 나가지고 currentParticipants가 1 감소하며 알림이 전송된다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }
        }

        Given("호스트가 링크를 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("링크를 나가면") {
                Then("CannotLeaveAsHostException이 발생한다") {
                    shouldThrow<CannotLeaveAsHostException> {
                        leenkUsecase.leaveLeenk(host.id, leenk.id)
                    }
                }
            }
        }

        Given("참여하지 않은 사용자가 링크를 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val nonParticipant = persistUser(2L, "미참여자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            When("링크를 나가면") {
                Then("LeenkParticipantNotFoundException이 발생한다") {
                    shouldThrow<LeenkParticipantNotFoundException> {
                        leenkUsecase.leaveLeenk(nonParticipant.id, leenk.id)
                    }
                }
            }
        }

        Given("마감된 링크에서 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.CLOSED)

            When("링크를 나가면") {
                Then("LeenkNotRecruitingException이 발생한다") {
                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.leaveLeenk(otherUser.id, leenk.id)
                    }
                }
            }
        }

        Given("완료된 링크에서 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val otherUser = persistUser(2L, "다른 사용자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.FINISHED)

            When("링크를 나가면") {
                Then("LeenkNotRecruitingException이 발생한다") {
                    shouldThrow<LeenkNotRecruitingException> {
                        leenkUsecase.leaveLeenk(otherUser.id, leenk.id)
                    }
                }
            }
        }

        Given("마지막 일반 참여자가 링크를 나가는 경우") {
            val host = persistUser(id = 1L, name = "호스트")
            val lastParticipant = persistUser(2L, "마지막참여자")
            val leenk = persistLeenk(author = host, status = LeenkStatus.RECRUITING)

            // 참여자 추가 (host + lastParticipant = 2명)
            leenkUsecase.participateLeenk(lastParticipant.id, leenk.id)

            // DB에서 최신 currentParticipants 가져오기
            val updatedAfterParticipate = leenkRepository.findById(leenk.id!!).get()
            val initialCount = updatedAfterParticipate.currentParticipants

            When("링크를 나가면") {
                leenkUsecase.leaveLeenk(lastParticipant.id, leenk.id)

                Then("호스트만 남고 currentParticipants가 1 감소한다") {
                    val updated = leenkRepository.findById(leenk.id!!).get()
                    // host만 남음
                    updated.currentParticipants shouldBe initialCount - 1
                }
            }
        }
    })
