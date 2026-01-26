package leets.leenk.domain.leenk.domain.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.user.test.fixture.UserTestFixture

class LeenkTest : StringSpec({

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
            currentParticipants = 2L
        )

    "increaseCurrentParticipants 호출 시 현재 참여자 수가 1 증가한다" {
        // Given : 현재 참여자가 2명인 모집 중인 링크
        val leenk = createLeenk()
        val initialParticipants = leenk.currentParticipants

        // When : 현재 참여자 수를 1 증가시킨다
        leenk.increaseCurrentParticipants()

        // Then : 현재 참여자 수가 1 증가했는지 확인한다
        leenk.currentParticipants shouldBe initialParticipants + 1
    }

    "decreaseCurrentParticipants 호출 시 현재 참여자 수가 1 감소한다" {
        // Given: 현재 참여자가 2명인 모집 중인 링크
        val leenk = createLeenk()
        val initialParticipants = leenk.currentParticipants

        // When: 현재 참여자 수를 1 감소 시킨다
        leenk.decreaseCurrentParticipants()

        // Then: 현재 참여자 수가 1 감소했는지 확인한다
        leenk.currentParticipants shouldBe initialParticipants - 1
    }

    "changeStatusToClosed 호출 시 상태가 RECRUITING에서 CLOSED로 변경된다" {
        // Given: RECRUITING 상태의 링크
        val leenk = createLeenk()

        // When: 링크를 마감 처리한다
        leenk.changeStatusToClosed()

        // Then: 링크 상태가 CLOSED로 변경됐는지 확인한다
        leenk.status shouldBe LeenkStatus.CLOSED
    }

    "changeStatusToFinished 호출 시 상태가 FINISHED로 변경된다" {
        // Given : RECRUITING 상태의 링크
        val leenk = createLeenk()

        // When : 링크를 종료 처리한다
        leenk.changeStatusToFinished()

        // Then : 링크 상태가 FINISHED로 변경됐는지 확인한다
        leenk.status shouldBe LeenkStatus.FINISHED
    }
})
