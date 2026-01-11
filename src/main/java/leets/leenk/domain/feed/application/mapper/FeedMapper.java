package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.*;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.media.application.dto.response.FeedMediaResponse;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static leets.leenk.domain.feed.application.util.FeedDescriptionUtil.normalizeDescription;

@Component
@RequiredArgsConstructor
public class FeedMapper {
    private final UserProfileMapper userProfileMapper;

    public FeedListResponse toFeedListResponse(Slice<Feed> slice, Map<Long, List<Media>> mediaMap) {
        List<FeedResponse> responses = toFeedListResponse(slice.getContent(), mediaMap);

        return FeedListResponse.builder()
                .feeds(responses)
                .pageable(PageableMapperUtil.from(slice))
                .build();
    }

    public FeedListResponse toFeedListResponse(User user, Slice<Feed> slice, Map<Long, List<Media>> mediaMap, boolean includeTotalReaction) {
        List<FeedResponse> responses = toFeedListResponse(slice.getContent(), mediaMap);

        return FeedListResponse.builder()
                .totalReactionCount(includeTotalReaction ? user.getTotalReactionCount() : null)
                .feeds(responses)
                .pageable(PageableMapperUtil.from(slice))
                .build();
    }

    private List<FeedResponse> toFeedListResponse(List<Feed> feeds, Map<Long, List<Media>> mediaMap) {
        return feeds.stream()
                .map(feed -> {
                    List<Media> medias = mediaMap.getOrDefault(feed.getId(), List.of());
                    Media thumbnail = medias.stream().findFirst().orElse(null);

                    return toFeedResponse(feed, thumbnail);
                })
                .toList();
    }

    public FeedResponse toFeedResponse(Feed feed, Media thumbNail) {
        return FeedResponse.builder()
                .feedId(feed.getId())
                .author(toFeedAuthorResponse(feed))
                .thumbNail(thumbNail.getThumbnailUrl())
                .totalReactionCount(feed.getTotalReactionCount())
                .build();
    }

    public FeedAuthorResponse toFeedAuthorResponse(Feed feed) {
        return FeedAuthorResponse.builder()
                .author(userProfileMapper.toProfile(feed.getUser()))
                .build();
    }

    public Feed toFeed(User user, String description) {
        String normalizedDescription = normalizeDescription(description);

        return Feed.builder()
                .user(user)
                .description(normalizedDescription)
                .build();
    }

    public FeedDetailResponse toFeedDetailResponse(Feed feed, List<Media> medias, List<LinkedUser> linkedUsers, List<Comment> comments) {
        return FeedDetailResponse.builder()
                .feedId(feed.getId())
                .author(toFeedAuthorResponse(feed))
                .description(feed.getDescription())
                .totalReactionCount(feed.getTotalReactionCount())
                .createdAt(feed.getCreateDate())
                .media(toFeedMediaResponses(medias))
                .linkedUserCount(linkedUsers.size())
                .linkedUser(toLinkedUserResponses(linkedUsers, feed))
                .comments(toGetCommentsResponses(comments))
                .build();
    }

    private List<FeedMediaResponse> toFeedMediaResponses(List<Media> medias) {
        return medias.stream()
                .map(media -> FeedMediaResponse.builder()
                        .position(media.getPosition())
                        .mediaUrl(media.getMediaUrl())
                        .mediaType(media.getMediaType())
                        .build())
                .toList();
    }

    private List<LinkedUserResponse> toLinkedUserResponses(List<LinkedUser> linkedUsers, Feed feed) {
        Long authorId = feed.getUser().getId();

        return linkedUsers.stream()
                .map(linkedUser -> LinkedUserResponse.builder()
                        .user(userProfileMapper.toProfile(linkedUser.getUser()))
                        .isAuthor(linkedUser.getUser().getId().equals(feed.getUser().getId()))
                        .build())
                .toList();
    }

    private List<FeedCommentResponse> toGetCommentsResponses(List<Comment> comments) {
        return comments.stream()
                .map(comment -> FeedCommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .user(userProfileMapper.toProfile(comment.getUser()))
                        .comment(comment.getComment())
                        .createdAt(comment.getCreateDate())
                        .build())
                .toList();
    }

    public FeedNavigationResponse toFeedNavigationResponse(
            Feed currentFeed,
            List<Feed> prevFeeds,
            List<Feed> nextFeeds,
            Map<Long, List<Media>> mediaMap,
            Map<Long, List<LinkedUser>> linkedUserMap,
            Map<Long, List<Comment>> commentsMap,
            boolean hasMorePrev,
            boolean hasMoreNext
    ) {
        FeedDetailResponse current = toFeedDetailResponse(
                currentFeed,
                mediaMap.getOrDefault(currentFeed.getId(), List.of()),
                linkedUserMap.getOrDefault(currentFeed.getId(), List.of()),
                commentsMap.getOrDefault(currentFeed.getId(), List.of())
        );

        List<FeedDetailResponse> prevFeedResponses = prevFeeds.stream()
                .map(feed -> toFeedDetailResponse(
                        feed,
                        mediaMap.getOrDefault(feed.getId(), List.of()),
                        linkedUserMap.getOrDefault(feed.getId(), List.of()),
                        commentsMap.getOrDefault(feed.getId(), List.of())
                ))
                .toList();

        List<FeedDetailResponse> nextFeedResponses = nextFeeds.stream()
                .map(feed -> toFeedDetailResponse(
                        feed,
                        mediaMap.getOrDefault(feed.getId(), List.of()),
                        linkedUserMap.getOrDefault(feed.getId(), List.of()),
                        commentsMap.getOrDefault(feed.getId(), List.of())
                ))
                .toList();

        return FeedNavigationResponse.builder()
                .current(current)
                .prevFeeds(prevFeedResponses)
                .nextFeeds(nextFeedResponses)
                .hasMorePrev(hasMorePrev)
                .hasMoreNext(hasMoreNext)
                .build();
    }
}
