package leets.leenk.domain.feed.domain.service

import jakarta.persistence.PessimisticLockException
import leets.leenk.domain.feed.application.exception.FeedNotFoundException
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.entity.UserBlock
import leets.leenk.global.common.exception.ResourceLockedException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class FeedGetService(
    private val feedRepository: FeedRepository,
) {

    fun findById(feedId: Long): Feed {
        return feedRepository.findByDeletedAtIsNullAndId(feedId)
            .orElseThrow { FeedNotFoundException() }
    }

    /**
     * 비관적 락을 사용하여 피드 조회
     * 동시 수정이 발생할 수 있는 경우 (공감하기 등) 사용
     */
    fun findByIdWithLock(feedId: Long): Feed {
        return try {
            feedRepository.findByIdWithPessimisticLock(feedId)
                .orElseThrow { FeedNotFoundException() }
        } catch (e: PessimisticLockException) {
            throw ResourceLockedException()
        }
    }

    fun findAll(pageable: Pageable, blockedUser: List<UserBlock>): Slice<Feed> {
        val blockedUserIds = blockedUser
            .map { it.blocked }
            .map { it.id!! }

        return feedRepository.findAllByDeletedAtIsNullWithUser(pageable, blockedUserIds)
    }

    fun findAllByUser(user: User, pageable: Pageable): Slice<Feed> {
        return feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable)
    }

    /**
     * 이전 피드 조회 (더 최신) - hasMore 정보 포함
     * ASC로 조회한 결과를 최신순(DESC)으로 역정렬하여 반환
     * 한 번의 쿼리로 피드 목록과 추가 피드 존재 여부를 함께 반환
     */
    fun findPrevFeedsWithHasMore(currentFeed: Feed, blockedUsers: List<UserBlock>, size: Int): FeedNavigationResult {
        val blockedUserIds = extractBlockedUserIds(blockedUsers)

        // size+1 개를 조회하여 hasMore 판단
        val pageable = PageRequest.of(0, size + 1)
        var feeds = feedRepository.findPrevFeeds(
            currentFeed.createDate!!,
            blockedUserIds.takeIf { it.isNotEmpty() },
            pageable,
        )

        // hasMore 판단
        val hasMore = feeds.size > size

        // size보다 많으면 잘라내기
        if (hasMore) {
            feeds = feeds.subList(0, size)
        }

        // ASC로 조회했으므로 최신순으로 역정렬
        feeds = feeds.reversed()

        return FeedNavigationResult(feeds, hasMore)
    }

    /**
     * 다음 피드 조회 (더 오래된) - hasMore 정보 포함
     * DESC로 조회하므로 그대로 반환
     * 한 번의 쿼리로 피드 목록과 추가 피드 존재 여부를 함께 반환
     */
    fun findNextFeedsWithHasMore(currentFeed: Feed, blockedUsers: List<UserBlock>, size: Int): FeedNavigationResult {
        val blockedUserIds = extractBlockedUserIds(blockedUsers)

        // size+1 개를 조회하여 hasMore 판단
        val pageable = PageRequest.of(0, size + 1)
        var feeds = feedRepository.findNextFeeds(
            currentFeed.createDate!!,
            blockedUserIds.takeIf { it.isNotEmpty() },
            pageable,
        )

        // hasMore 판단
        val hasMore = feeds.size > size

        // size보다 많으면 잘라내기
        if (hasMore) {
            feeds = feeds.subList(0, size)
        }

        return FeedNavigationResult(feeds, hasMore)
    }

    private fun extractBlockedUserIds(blockedUsers: List<UserBlock>): List<Long> {
        return blockedUsers
            .map { it.blocked }
            .map { it.id!! }
    }
}
