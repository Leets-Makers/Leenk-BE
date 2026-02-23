package leets.leenk.domain.leenk.domain.repository

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LeenkRepository : JpaRepository<Leenk, Long> {
    fun findAllByStatus(
        status: LeenkStatus,
        pageable: Pageable,
    ): Slice<Leenk>

    fun findAllByStatusIn(
        statuses: List<LeenkStatus>,
        pageable: Pageable,
    ): Slice<Leenk>

    fun findAllByStatusInAndStartTimeLessThanEqual(
        statuses: List<LeenkStatus>,
        startTime: LocalDateTime,
    ): List<Leenk>

    fun findAllByStatusAndStartTimeGreaterThanAndStartTimeLessThanEqual(
        status: LeenkStatus,
        startTimeAfter: LocalDateTime,
        startTimeBefore: LocalDateTime,
    ): List<Leenk>

    fun findAllByStatusInAndStartTimeGreaterThanAndStartTimeLessThanEqual(
        statuses: List<LeenkStatus>,
        now: LocalDateTime,
        startTime: LocalDateTime,
    ): List<Leenk>
}
