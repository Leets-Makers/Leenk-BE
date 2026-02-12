package leets.leenk.domain.feed.application.usecase

import leets.leenk.domain.feed.application.dto.request.CommentWriteRequest
import leets.leenk.domain.feed.application.dto.request.FeedReportRequest
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest
import leets.leenk.domain.feed.application.dto.request.FeedUploadRequest
import leets.leenk.domain.feed.application.dto.request.ReactionRequest
import leets.leenk.domain.feed.application.dto.response.FeedDetailResponse
import leets.leenk.domain.feed.application.dto.response.FeedListResponse
import leets.leenk.domain.feed.application.dto.response.FeedNavigationResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse
import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse
import leets.leenk.domain.feed.application.exception.CommentDeleteNotAllowedException
import leets.leenk.domain.feed.application.exception.FeedDeleteNotAllowedException
import leets.leenk.domain.feed.application.exception.FeedUpdateNotAllowedException
import leets.leenk.domain.feed.application.exception.SelfReactionNotAllowedException
import leets.leenk.domain.feed.application.mapper.CommentMapper
import leets.leenk.domain.feed.application.mapper.FeedMapper
import leets.leenk.domain.feed.application.mapper.FeedUserMapper
import leets.leenk.domain.feed.application.mapper.ReactionMapper
import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.feed.domain.event.FeedDomainEvent
import leets.leenk.domain.feed.domain.service.CommentDeleteService
import leets.leenk.domain.feed.domain.service.CommentGetService
import leets.leenk.domain.feed.domain.service.CommentSaveService
import leets.leenk.domain.feed.domain.service.FeedDeleteService
import leets.leenk.domain.feed.domain.service.FeedGetService
import leets.leenk.domain.feed.domain.service.FeedSaveService
import leets.leenk.domain.feed.domain.service.FeedUpdateService
import leets.leenk.domain.feed.domain.service.LinkedUserDeleteService
import leets.leenk.domain.feed.domain.service.LinkedUserGetService
import leets.leenk.domain.feed.domain.service.LinkedUserSaveService
import leets.leenk.domain.feed.domain.service.ReactionGetService
import leets.leenk.domain.feed.domain.service.ReactionSaveService
import leets.leenk.domain.media.application.mapper.MediaMapper
import leets.leenk.domain.media.domain.service.MediaDeleteService
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaSaveService
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.NotionDatabaseService
import leets.leenk.domain.user.domain.service.SlackWebhookService
import leets.leenk.domain.user.domain.service.blockuser.UserBlockService
import leets.leenk.domain.user.domain.service.user.UserGetService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedUsecase(
    private val userGetService: UserGetService,
    private val userBlockService: UserBlockService,
    private val slackWebhookService: SlackWebhookService,
    private val notionDatabaseService: NotionDatabaseService,
    private val feedGetService: FeedGetService,
    private val feedSaveService: FeedSaveService,
    private val feedUpdateService: FeedUpdateService,
    private val feedDeleteService: FeedDeleteService,
    private val mediaGetService: MediaGetService,
    private val mediaSaveService: MediaSaveService,
    private val mediaDeleteService: MediaDeleteService,
    private val linkedUserGetService: LinkedUserGetService,
    private val linkedUserSaveService: LinkedUserSaveService,
    private val linkedUserDeleteService: LinkedUserDeleteService,
    private val reactionGetService: ReactionGetService,
    private val reactionSaveService: ReactionSaveService,
    private val commentSaveService: CommentSaveService,
    private val commentGetService: CommentGetService,
    private val commentDeleteService: CommentDeleteService,
    private val feedMapper: FeedMapper,
    private val mediaMapper: MediaMapper,
    private val feedUserMapper: FeedUserMapper,
    private val reactionMapper: ReactionMapper,
    private val commentMapper: CommentMapper,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun getFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): FeedListResponse {
        val user = userGetService.findById(userId)
        val blockedUsers = userBlockService.findAllByBlocker(user)

        val pageable = PageRequest.of(pageNumber, pageSize)
        val slice = feedGetService.findAll(pageable, blockedUsers)

        val feeds = slice.content
        val medias = mediaGetService.findAllByFeeds(feeds)

        val mediaMap = medias.groupBy { it.feed.id!! }

        return feedMapper.toFeedListResponse(slice, mediaMap)
    }

    @Transactional(readOnly = true)
    fun getFeedDetail(feedId: Long): FeedDetailResponse {
        val feed = feedGetService.findById(feedId)
        val medias = mediaGetService.findAllByFeed(feed)
        val linkedUsers = linkedUserGetService.findAll(feed)
        val comments = commentGetService.findAllByFeed(feed)

        return feedMapper.toFeedDetailResponse(feed, medias, linkedUsers, comments)
    }

    @Transactional(readOnly = true)
    fun getFeedNavigation(
        feedId: Long,
        currentUserId: Long,
        prevSize: Int?,
        nextSize: Int?,
    ): FeedNavigationResponse {
        // 파라미터 검증 및 기본값 설정
        val validatedPrevSize = validateSize(prevSize, DEFAULT_NAVIGATION_SIZE, MAX_NAVIGATION_SIZE)
        val validatedNextSize = validateSize(nextSize, DEFAULT_NAVIGATION_SIZE, MAX_NAVIGATION_SIZE)

        // 현재 피드 조회
        val currentFeed = feedGetService.findById(feedId)

        // 차단 사용자 목록 조회
        val currentUser = userGetService.findById(currentUserId)
        val blockedUsers = userBlockService.findAllByBlocker(currentUser)

        // 이전/다음 피드 조회 (hasMore 정보 포함)
        val prevResult =
            feedGetService.findPrevFeedsWithHasMore(
                currentFeed,
                blockedUsers,
                validatedPrevSize,
            )
        val nextResult =
            feedGetService.findNextFeedsWithHasMore(
                currentFeed,
                blockedUsers,
                validatedNextSize,
            )

        val prevFeeds = prevResult.feeds
        val nextFeeds = nextResult.feeds
        val hasMorePrev = prevResult.hasMore
        val hasMoreNext = nextResult.hasMore

        // 모든 피드의 미디어와 링크된 사용자 조회
        val allFeeds = mutableListOf<Feed>()
        allFeeds.add(currentFeed)
        allFeeds.addAll(prevFeeds)
        allFeeds.addAll(nextFeeds)

        val allMedias = mediaGetService.findAllByFeeds(allFeeds)
        val mediaMap = allMedias.groupBy { it.feed.id!! }

        val linkedUserMap = mutableMapOf<Long, List<LinkedUser>>()
        for (feed in allFeeds) {
            val linkedUsers = linkedUserGetService.findAll(feed)
            linkedUserMap[feed.id!!] = linkedUsers
        }

        val commentsMap = mutableMapOf<Long, List<Comment>>()
        for (feed in allFeeds) {
            val comments = commentGetService.findAllByFeed(feed)
            commentsMap[feed.id!!] = comments
        }

        return feedMapper.toFeedNavigationResponse(
            currentFeed,
            prevFeeds,
            nextFeeds,
            mediaMap,
            linkedUserMap,
            commentsMap,
            hasMorePrev,
            hasMoreNext,
        )
    }

    private fun validateSize(
        size: Int?,
        defaultValue: Int,
        maxValue: Int,
    ): Int {
        if (size == null) {
            return defaultValue
        }
        if (size < 0) {
            return 0
        }
        if (size > maxValue) {
            return maxValue
        }
        return size
    }

    @Transactional
    fun uploadFeed(
        userId: Long,
        request: FeedUploadRequest,
    ) {
        val author = userGetService.findById(userId)
        val feed = feedMapper.toFeed(author, request.description)
        feedSaveService.save(feed)

        val medias =
            request.media.map { mediaRequest ->
                mediaMapper.toMedia(feed, mediaRequest)
            }
        mediaSaveService.saveAll(medias)

        val linkedUsers = getLinkedUsers(author, request.userIds, feed)
        linkedUserSaveService.saveAll(linkedUsers)

        // 태그된 사용자 ID 목록 (작성자 제외)
        val taggedUserIds = request.userIds.filter { it != author.id }

        eventPublisher.publishEvent(
            FeedDomainEvent.Created(
                feedId = feed.id!!,
                authorId = author.id!!,
                authorName = author.name,
                taggedUserIds = taggedUserIds,
            ),
        )
    }

    private fun getLinkedUsers(
        author: User,
        userIds: List<Long>,
        feed: Feed,
    ): List<LinkedUser> {
        val users = userGetService.findAll(userIds).toMutableSet()
        users.add(author) // 중복 자동 제거

        return users.map { user ->
            feedUserMapper.toLinkedUser(user, feed)
        }
    }

    /**
     * 피드에 공감을 추가
     * 비관적 락(PESSIMISTIC_WRITE)을 사용하여 동시성 문제를 해결
     * @see leets.leenk.domain.feed.domain.repository.FeedRepository.findByIdWithPessimisticLock
     * @see leets.leenk.domain.user.domain.repository.UserRepository.findByIdWithPessimisticLock
     */
    @Transactional
    fun reactToFeed(
        userId: Long,
        feedId: Long,
        request: ReactionRequest,
    ) {
        val feed = feedGetService.findByIdWithLock(feedId) // !락 순서 중요!
        val user = userGetService.findById(userId)

        validateReaction(feed, user)

        val reaction =
            reactionGetService
                .findByFeedAndUser(feed, user)
                .orElseGet {
                    reactionSaveService.save(
                        reactionMapper.toReaction(user, feed, 0L),
                    )
                }

        val previousReactionCount = reaction.reactionCount

        // Feed를 가져올 때 Fetch Join으로 작성자를 함께 가져와 락이 함께 걸리므로 별도의 락 필요 없음.
        feedUpdateService.updateTotalReaction(feed, reaction, feed.user, request.reactionCount)

        // 업데이트된 총 공감 수 계산
        val totalReactionCount = feed.totalReactionCount + request.reactionCount

        eventPublisher.publishEvent(
            FeedDomainEvent.Reacted(
                feedId = feed.id!!,
                feedAuthorId = feed.user.id!!,
                reactorId = user.id!!,
                reactorName = user.name,
                previousReactionCount = previousReactionCount,
                totalReactionCount = totalReactionCount,
            ),
        )
    }

    @Transactional
    fun writeComment(
        userId: Long,
        feedId: Long,
        request: CommentWriteRequest,
    ) {
        val user = userGetService.findById(userId)
        val feed = feedGetService.findById(feedId)

        val comment = commentMapper.toComment(user, feed, request)

        commentSaveService.saveComment(comment)
    }

    private fun validateReaction(
        feed: Feed,
        user: User,
    ) {
        if (feed.user == user) {
            throw SelfReactionNotAllowedException()
        }
    }

    @Transactional(readOnly = true)
    fun getReactionUser(feedId: Long): List<ReactionUserResponse> {
        val feed = feedGetService.findById(feedId)
        val reactions = reactionGetService.findAll(feed)

        return reactions.map { reactionMapper.toResponse(it) }
    }

    @Transactional
    fun updateFeed(
        userId: Long,
        feedId: Long,
        request: FeedUpdateRequest,
    ) {
        val feed = feedGetService.findById(feedId)
        val author = userGetService.findById(userId)

        checkAuthor(author, feed)

        feedUpdateService.update(feed, request)

        if (request.media != null) {
            mediaDeleteService.deleteAllByFeed(feed)
            val newMedias =
                request.media.map { mediaRequest ->
                    mediaMapper.toMedia(feed, mediaRequest)
                }
            mediaSaveService.saveAll(newMedias)
        }

        if (request.userIds != null) {
            linkedUserDeleteService.deleteAllByFeed(feed)
            val newLinkedUsers = getLinkedUsers(author, request.userIds, feed)
            linkedUserSaveService.saveAll(newLinkedUsers)
        }
    }

    @Transactional(readOnly = true)
    fun getMyFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): FeedListResponse = getFeedsByUser(userId, pageNumber, pageSize, true)

    @Transactional(readOnly = true)
    fun getOthersFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): FeedListResponse = getFeedsByUser(userId, pageNumber, pageSize, false)

    private fun getFeedsByUser(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
        includeTotalReaction: Boolean,
    ): FeedListResponse {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val user = userGetService.findById(userId)

        val slice = feedGetService.findAllByUser(user, pageable)
        val medias = mediaGetService.findAllByFeeds(slice.content)

        val mediaMap = medias.groupBy { it.feed.id!! }

        return feedMapper.toFeedListResponse(user, slice, mediaMap, includeTotalReaction)
    }

    @Transactional(readOnly = true)
    fun getLinkedFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): FeedListResponse {
        val user = userGetService.findById(userId)
        val pageable = PageRequest.of(pageNumber, pageSize)

        val slice = linkedUserGetService.findAllByUser(user, pageable)
        val medias = mediaGetService.findAllByFeeds(slice.content)

        val mediaMap = medias.groupBy { it.feed.id!! }

        return feedMapper.toFeedListResponse(slice, mediaMap)
    }

    @Transactional(readOnly = true)
    fun getAllUser(): List<FeedUserResponse> {
        val users = userGetService.findAll()

        return users.map { feedUserMapper.toFeedUserResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getUsers(
        pageNumber: Int,
        pageSize: Int,
    ): FeedUserListResponse {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val slice = userGetService.findAll(pageable)

        return feedUserMapper.toFeedUserListResponse(slice)
    }

    @Transactional
    fun deleteFeed(
        userId: Long,
        feedId: Long,
    ) {
        val feed = feedGetService.findById(feedId)
        val user = userGetService.findById(userId)

        if (feed.user != user) {
            throw FeedDeleteNotAllowedException()
        }

        feedDeleteService.delete(feed)
    }

    @Transactional
    fun deleteComment(
        userId: Long,
        commentId: Long,
    ) {
        val user = userGetService.findById(userId)
        val comment = commentGetService.findCommentByIdNotDeleted(commentId)

        if (comment.user != user) {
            throw CommentDeleteNotAllowedException()
        }

        commentDeleteService.deleteComment(comment)
    }

    @Transactional(readOnly = true)
    fun reportFeed(
        userId: Long,
        feedId: Long,
        request: FeedReportRequest,
    ) {
        val user = userGetService.findById(userId)
        val feed = feedGetService.findById(feedId)

        notionDatabaseService.sendFeedReport(request.report, user.id!!, feed.id!!)
        slackWebhookService.sendFeedReport(request.report)
    }

    private fun checkAuthor(
        user: User,
        feed: Feed,
    ) {
        if (feed.user != user) {
            throw FeedUpdateNotAllowedException()
        }
    }

    companion object {
        private const val MAX_NAVIGATION_SIZE = 3
        private const val DEFAULT_NAVIGATION_SIZE = 1
    }
}
