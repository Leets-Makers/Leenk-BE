package leets.leenk.domain.leenk.domain.service

import io.kotest.assertions.throwables.shouldNotThrow
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
        Given("현재 참여자가 5명인 링크") {

            When("최대 참여자를 현재 참여자보다 많은 8명으로 변경하면") {
                val leenk = createLeenk() // current: 5명
                val location = leenk.location

                val request = updateRequest(maxParticipants = 8L)

                Then("예외 없이 최대 참여자가 요청한 값으로 변경된다") {
                    shouldNotThrow<MaxParticipantsTooLowException> {
                        leenkUpdateService.updateLeenk(leenk, location, request)
                    }
                    leenk.maxParticipants shouldBe request.maxParticipants
                }
            }

            When("최대 참여자를 현재 참여자와 같은 5명으로 변경하면") {
                val leenk = createLeenk() // current: 5명
                val location = leenk.location

                val request = updateRequest(maxParticipants = 5L)

                Then("예외 없이 최대 참여자가 요청한 값으로 변경된다") {
                    shouldNotThrow<MaxParticipantsTooLowException> {
                        leenkUpdateService.updateLeenk(leenk, location, request)
                    }
                    leenk.maxParticipants shouldBe request.maxParticipants
                }
            }

            When("최대 참여자를 현재 참여자보다 적은 3명으로 변경하려 하면") {
                val leenk = createLeenk() // current: 5명
                val location = leenk.location

                val request = updateRequest(maxParticipants = 3L)

                Then("MaxParticipantsTooLowException이 발생하고 변경되지 않는다") {
                    shouldThrow<MaxParticipantsTooLowException> {
                        leenkUpdateService.updateLeenk(leenk, location, request)
                    }
                }
            }
        }
    })
