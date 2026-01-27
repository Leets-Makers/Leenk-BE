package leets.leenk.domain.leenk.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest
import leets.leenk.domain.leenk.application.exception.MaxParticipantsTooLowException
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import java.time.LocalDateTime

class LeenkUpdateServiceTest :
    BehaviorSpec({

        val leenkUpdateService = LeenkUpdateService()

        // 헬퍼: 기본 사용자
        fun defaultUser() = UserTestFixture.createUser(1L, "김철수")

        // 헬퍼: 기본 위치
        fun defaultLocation() = LocationTestFixture.createLocation(1L, "서울")

        // 헬퍼: 기본 Leenk 생성
        fun createLeenk() =
            LeenkTestFixture.createLeenk(
                id = 1L,
                author = defaultUser(),
                location = defaultLocation(),
                currentParticipants = 5L,
                maxParticipants = 10L,
            )

        // 헬퍼: LeenkUpdateRequest 생성용
        fun updateRequest(
            title: String? = null,
            content: String? = null,
            placeName: String? = null,
            startTime: LocalDateTime? = null,
            maxParticipants: Long? = null,
            mediaUrl: String? = null,
        ) = LeenkUpdateRequest(
            title,
            content,
            placeName,
            startTime,
            maxParticipants,
            mediaUrl,
        )

        // 최대 참여자 수 검증 로직을 테스트
        Given("링크 현재 참여자 5명, 최대 참여자를 8명으로 변경하는 경우") {
            val leenk = createLeenk()
            val location = leenk.location

            val request = updateRequest(maxParticipants = 8L)

            When("최대 참여자를 현재 참여자보다 많게 변경하면") {
                leenkUpdateService.updateLeenk(leenk, location, request)

                Then("최대 참여자가 요청한 값으로 변경된다") {
                    leenk.maxParticipants shouldBe request.maxParticipants
                }
            }
        }

        Given("링크 현재 참여자 5명, 최대 참여자를 5명으로 변경하는 경우") {
            val leenk = createLeenk()
            val location = leenk.location

            val request = updateRequest(maxParticipants = 5L)

            When("최대 참여자를 현재 참여자와 같게 변경하면") {
                leenkUpdateService.updateLeenk(leenk, location, request)

                Then("최대 참여자가 요청한 값으로 변경된다") {
                    leenk.maxParticipants shouldBe request.maxParticipants
                }
            }
        }

        Given("링크 현재 참여자 5명, 최대 참여자를 3명으로 변경하는 경우") {
            val leenk = createLeenk()
            val location = leenk.location

            val request = updateRequest(maxParticipants = 3L)

            When("최대 참여자를 현재 참여자보다 적게 변경하면") {
                Then("MaxParticipantsTooLowException이 발생한다") {
                    shouldThrow<MaxParticipantsTooLowException> {
                        leenkUpdateService.updateLeenk(leenk, location, request)
                    }
                }
            }
        }
    })
