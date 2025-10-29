package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.*;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.media.application.dto.response.FeedMediaResponse;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static leets.leenk.domain.feed.application.util.FeedDescriptionUtil.normalizeDescription;

@Component
public class FeedMapper {

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
                .userId(feed.getUser().getId())
                .profileImage(feed.getUser().getProfileImage())
                .name(feed.getUser().getName())
                .build();
    }

    public Feed toFeed(User user, String description) {
        String normalizedDescription = normalizeDescription(description);

        return Feed.builder()
                .user(user)
                .description(normalizedDescription)
                .build();
    }

    public FeedDetailResponse toFeedDetailResponse(Feed feed, List<Media> medias, List<LinkedUser> linkedUsers) {
        return FeedDetailResponse.builder()
                .feedId(feed.getId())
                .author(toFeedAuthorResponse(feed))
                .description(feed.getDescription())
                .totalReactionCount(feed.getTotalReactionCount())
                .createdAt(feed.getCreateDate())
                .media(toFeedMediaResponses(medias))
                .linkedUserCount(linkedUsers.size())
                .linkedUser(toLinkedUserResponses(linkedUsers, feed))
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
        return linkedUsers.stream()
                .map(linkedUser -> LinkedUserResponse.builder()
                        .userId(linkedUser.getUser().getId())
                        .isAuthor(linkedUser.getUser().getId().equals(feed.getUser().getId()))
                        .profileImage(linkedUser.getUser().getProfileImage())
                        .name(linkedUser.getUser().getName())
                        .build())
                .toList();
    }
}
