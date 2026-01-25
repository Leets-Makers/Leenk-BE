package leets.leenk.domain.feed.application.mapper

import leets.leenk.domain.feed.application.dto.response.FeedAuthorResponse
import leets.leenk.domain.feed.application.dto.response.FeedCommentResponse
import leets.leenk.domain.feed.application.dto.response.FeedDetailResponse
import leets.leenk.domain.feed.application.dto.response.FeedListResponse
import leets.leenk.domain.feed.application.dto.response.FeedNavigationResponse
import leets.leenk.domain.feed.application.dto.response.FeedResponse
import leets.leenk.domain.feed.application.dto.response.LinkedUserResponse
import leets.leenk.domain.feed.application.util.FeedDescriptionUtil.normalizeDescription
import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.media.application.dto.response.FeedMediaResponse
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.dto.PageableMapperUtil
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class FeedMapper(
    private val userProfileMapper: UserProfileMapper,
) {

    fun toFeedListResponse(slice: Slice<Feed>, mediaMap: Map<Long, List<Media>>): FeedListResponse {
        val responses = toFeedListResponse(slice.content, mediaMap)

        return FeedListResponse(
            totalReactionCount = null,
            feeds = responses,
            pageable = PageableMapperUtil.from(slice),
        )
    }

    fun toFeedListResponse(
        user: User,
        slice: Slice<Feed>,
        mediaMap: Map<Long, List<Media>>,
        includeTotalReaction: Boolean,
    ): FeedListResponse {
        val responses = toFeedListResponse(slice.content, mediaMap)

        return FeedListResponse(
            totalReactionCount = if (includeTotalReaction) user.totalReactionCount else null,
            feeds = responses,
            pageable = PageableMapperUtil.from(slice),
        )
    }

    private fun toFeedListResponse(feeds: List<Feed>, mediaMap: Map<Long, List<Media>>): List<FeedResponse> {
        return feeds.map { feed ->
            val medias = mediaMap.getOrDefault(feed.id, emptyList())
            val thumbnail = medias.firstOrNull()

            toFeedResponse(feed, thumbnail)
        }
    }

    fun toFeedResponse(feed: Feed, thumbNail: Media?): FeedResponse {
        return FeedResponse(
            feedId = feed.id!!,
            author = toFeedAuthorResponse(feed),
            thumbNail = thumbNail!!.thumbnailUrl,
            totalReactionCount = feed.totalReactionCount,
        )
    }

    fun toFeedAuthorResponse(feed: Feed): FeedAuthorResponse {
        return FeedAuthorResponse(
            author = userProfileMapper.toProfile(feed.user),
        )
    }

    fun toFeed(user: User, description: String?): Feed {
        val normalizedDescription = normalizeDescription(description)

        return Feed(
            user = user,
            description = normalizedDescription,
        )
    }

    fun toFeedDetailResponse(
        feed: Feed,
        medias: List<Media>,
        linkedUsers: List<LinkedUser>,
        comments: List<Comment>,
    ): FeedDetailResponse {
        return FeedDetailResponse(
            feedId = feed.id!!,
            author = toFeedAuthorResponse(feed),
            description = feed.description,
            totalReactionCount = feed.totalReactionCount,
            createdAt = feed.createDate!!,
            media = toFeedMediaResponses(medias),
            linkedUserCount = linkedUsers.size.toLong(),
            linkedUser = toLinkedUserResponses(linkedUsers, feed),
            comments = toGetCommentsResponses(comments),
        )
    }

    private fun toFeedMediaResponses(medias: List<Media>): List<FeedMediaResponse> {
        return medias.map { media ->
            FeedMediaResponse(
                position = media.position,
                mediaUrl = media.mediaUrl,
                mediaType = media.mediaType,
            )
        }
    }

    private fun toLinkedUserResponses(linkedUsers: List<LinkedUser>, feed: Feed): List<LinkedUserResponse> {
        return linkedUsers.map { linkedUser ->
            LinkedUserResponse(
                user = userProfileMapper.toProfile(linkedUser.user),
                isAuthor = linkedUser.user.id == feed.user.id,
            )
        }
    }

    private fun toGetCommentsResponses(comments: List<Comment>): List<FeedCommentResponse> {
        return comments.map { comment ->
            FeedCommentResponse(
                commentId = comment.commentId!!,
                user = userProfileMapper.toProfile(comment.user),
                comment = comment.comment!!,
                createdAt = comment.createDate!!,
            )
        }
    }

    fun toFeedNavigationResponse(
        currentFeed: Feed,
        prevFeeds: List<Feed>,
        nextFeeds: List<Feed>,
        mediaMap: Map<Long, List<Media>>,
        linkedUserMap: Map<Long, List<LinkedUser>>,
        commentsMap: Map<Long, List<Comment>>,
        hasMorePrev: Boolean,
        hasMoreNext: Boolean,
    ): FeedNavigationResponse {
        val current = toFeedDetailResponse(
            currentFeed,
            mediaMap.getOrDefault(currentFeed.id, emptyList()),
            linkedUserMap.getOrDefault(currentFeed.id, emptyList()),
            commentsMap.getOrDefault(currentFeed.id, emptyList()),
        )

        val prevFeedResponses = prevFeeds.map { feed ->
            toFeedDetailResponse(
                feed,
                mediaMap.getOrDefault(feed.id, emptyList()),
                linkedUserMap.getOrDefault(feed.id, emptyList()),
                commentsMap.getOrDefault(feed.id, emptyList()),
            )
        }

        val nextFeedResponses = nextFeeds.map { feed ->
            toFeedDetailResponse(
                feed,
                mediaMap.getOrDefault(feed.id, emptyList()),
                linkedUserMap.getOrDefault(feed.id, emptyList()),
                commentsMap.getOrDefault(feed.id, emptyList()),
            )
        }

        return FeedNavigationResponse(
            current = current,
            prevFeeds = prevFeedResponses,
            nextFeeds = nextFeedResponses,
            hasMorePrev = hasMorePrev,
            hasMoreNext = hasMoreNext,
        )
    }
}
