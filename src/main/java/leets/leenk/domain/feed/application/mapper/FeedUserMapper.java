package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse;
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedUserMapper {

    public LinkedUser toLinkedUser(User user, Feed feed) {
        return LinkedUser.builder()
                .feed(feed)
                .user(user)
                .build();
    }

    public FeedUserResponse toFeedUserResponse(User user) {
        return FeedUserResponse.builder()
                .userId(user.getId())
                .profileImage(user.getThumbnail())
                .name(user.getName())
                .build();
    }

    public FeedUserListResponse toFeedUserListResponse(Slice<User> slice) {
        List<FeedUserResponse> responses = slice.getContent().stream()
                .map(this::toFeedUserResponse)
                .toList();

        return FeedUserListResponse.builder()
                .users(responses)
                .pageable(PageableMapperUtil.from(slice))
                .build();
    }
}
