package leets.leenk.domain.leenk.domain.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.user.test.fixture.UserTestFixture

class LeenkTest :
    StringSpec({

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
                currentParticipants = 2L,
            )

        "increaseCurrentParticipants 호출 시 현재 참여자 수가 1 증가한다" {
            val leenk = createLeenk()
            val initialParticipants = leenk.currentParticipants

            leenk.increaseCurrentParticipants()

            leenk.currentParticipants shouldBe initialParticipants + 1
        }

        "decreaseCurrentParticipants 호출 시 현재 참여자 수가 1 감소한다" {
            val leenk = createLeenk()
            val initialParticipants = leenk.currentParticipants

            leenk.decreaseCurrentParticipants()

            leenk.currentParticipants shouldBe initialParticipants - 1
        }

        "changeStatusToClosed 호출 시 상태가 RECRUITING에서 CLOSED로 변경된다" {
            val leenk = createLeenk()

            leenk.changeStatusToClosed()

            leenk.status shouldBe LeenkStatus.CLOSED
        }

        "changeStatusToFinished 호출 시 상태가 FINISHED로 변경된다" {
            val leenk = createLeenk()

            leenk.changeStatusToFinished()

            leenk.status shouldBe LeenkStatus.FINISHED
        }
    })
