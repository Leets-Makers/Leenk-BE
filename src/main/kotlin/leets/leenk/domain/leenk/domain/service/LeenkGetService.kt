package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.application.exception.LeenkNotFoundException
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import leets.leenk.domain.leenk.domain.repository.LeenkRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LeenkGetService(
    private val leenkRepository: LeenkRepository,
) {
    fun findById(leenkId: Long): Leenk =
        leenkRepository
            .findById(leenkId)
            .orElseThrow { LeenkNotFoundException() }

    fun findAll(pageable: Pageable): Slice<Leenk> = leenkRepository.findAllByStatusIn(ALL_STATUSES, pageable)

    fun findByStatus(
        status: LeenkStatus,
        pageable: Pageable,
    ): Slice<Leenk> = leenkRepository.findAllByStatus(status, pageable)

    fun findByStatusParam(
        filter: LeenkFilter,
        pageable: Pageable,
    ): Slice<Leenk> =
        if (filter == LeenkFilter.ALL) {
            leenkRepository.findAllByStatusIn(ALL_STATUSES, pageable)
        } else {
            leenkRepository.findAllByStatus(filter.leenkStatus!!, pageable)
        }

    fun findLeenksStartingWithin30Minutes(now: LocalDateTime): List<Leenk> =
        leenkRepository.findAllByStatusInAndStartTimeGreaterThanAndStartTimeLessThanEqual(
            listOf(LeenkStatus.RECRUITING, LeenkStatus.CLOSED),
            now,
            now.plusMinutes(30),
        )

    fun findDueLeenks(now: LocalDateTime): List<Leenk> =
        leenkRepository.findAllByStatusInAndStartTimeLessThanEqual(
            listOf(LeenkStatus.RECRUITING, LeenkStatus.CLOSED),
            now.minusHours(1),
        )

    fun findUnnotifiedFinishedLeenks(now: LocalDateTime): List<Leenk> =
        leenkRepository.findAllByStatusAndStartTimeGreaterThanAndStartTimeLessThanEqual(
            LeenkStatus.FINISHED,
            now.minusHours(25),
            now.minusHours(1),
        )

    fun findOverdueRecruitingLeenksToNotify(now: LocalDateTime): List<Leenk> =
        leenkRepository.findAllByStatusAndStartTimeGreaterThanAndStartTimeLessThanEqual(
            LeenkStatus.RECRUITING,
            now.minusMinutes(30),
            now,
        )

    companion object {
        private val ALL_STATUSES = listOf(LeenkStatus.RECRUITING, LeenkStatus.CLOSED)
    }
}
