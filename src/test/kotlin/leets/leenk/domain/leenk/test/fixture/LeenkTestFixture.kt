package leets.leenk.domain.leenk.test.fixture

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.Location
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.user.domain.entity.User
import java.time.LocalDateTime

class LeenkTestFixture {
    companion object {
        fun createLeenk(
            id: Long? = null,
            author: User,
            location: Location,
            title: String = "테스트 링크",
            content: String? = "테스트 내용",
            startTime: LocalDateTime = LocalDateTime.now().plusDays(1),
            maxParticipants: Long = 10L,
            currentParticipants: Long = 1L,
            status: LeenkStatus = LeenkStatus.RECRUITING,
        ): Leenk =
            Leenk(
                id = id,
                author = author,
                location = location,
                title = title,
                content = content,
                startTime = startTime,
                maxParticipants = maxParticipants,
                currentParticipants = currentParticipants,
                status = status,
            )

        fun createClosedLeenk(
            id: Long? = null,
            author: User,
            location: Location,
            currentParticipants: Long = 5L,
            maxParticipants: Long = 10L,
        ): Leenk =
            createLeenk(
                id = id,
                author = author,
                location = location,
                status = LeenkStatus.CLOSED,
                currentParticipants = currentParticipants,
                maxParticipants = maxParticipants,
            )

        fun createFullLeenk(
            id: Long? = null,
            author: User,
            location: Location,
            maxParticipants: Long = 10L,
        ): Leenk =
            createLeenk(
                id = id,
                author = author,
                location = location,
                status = LeenkStatus.RECRUITING,
                currentParticipants = maxParticipants,
                maxParticipants = maxParticipants,
            )

        fun createAlmostFullLeenk(
            id: Long? = null,
            author: User,
            location: Location,
            maxParticipants: Long = 10L,
        ): Leenk =
            createLeenk(
                id = id,
                author = author,
                location = location,
                status = LeenkStatus.RECRUITING,
                currentParticipants = maxParticipants - 1,
                maxParticipants = maxParticipants,
            )

        fun createFinishedLeenk(
            id: Long? = null,
            author: User,
            location: Location,
            currentParticipants: Long = 10L,
            maxParticipants: Long = 10L,
        ): Leenk =
            createLeenk(
                id = id,
                author = author,
                location = location,
                status = LeenkStatus.FINISHED,
                currentParticipants = currentParticipants,
                maxParticipants = maxParticipants,
            )
    }
}
