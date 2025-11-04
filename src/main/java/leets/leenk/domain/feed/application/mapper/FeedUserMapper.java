package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse;
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.dto.PageableMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedUserMapper {
    private final UserProfileMapper userProfileMapper;

    public LinkedUser toLinkedUser(User user, Feed feed) {
        return LinkedUser.builder()
                .feed(feed)
                .user(user)
                .build();
    }

    public FeedUserResponse toFeedUserResponse(User user) {
        return FeedUserResponse.builder()
                .user(userProfileMapper.toProfile(user))
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
