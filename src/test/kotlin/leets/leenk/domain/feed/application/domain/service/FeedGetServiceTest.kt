package leets.leenk.domain.feed.application.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.PessimisticLockException
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.domain.service.FeedGetService
import leets.leenk.global.common.exception.ResourceLockedException

class FeedGetServiceTest :
    DescribeSpec({
        val feedRepository = mockk<FeedRepository>()
        val feedGetService = FeedGetService(feedRepository)

        describe("피드 리액션 기능") {
            context("동시 요청 시 락 타임아웃 발생 시") {
                it("DB에서 락 예외가 발생하면 커스텀 예외로 변환해야 한다") {
                    every { feedRepository.findByIdWithPessimisticLock(any()) } throws PessimisticLockException()

                    shouldThrow<ResourceLockedException> {
                        feedGetService.findByIdWithLock(1L)
                    }
                }
            }
        }
    })
