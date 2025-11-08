package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionMapper {
    private final UserProfileMapper userProfileMapper;

    public ReactionUserResponse toResponse(Reaction reaction) {
        return ReactionUserResponse.builder()
                .user(userProfileMapper.toProfile(reaction.getUser()))
                .reactionCount(reaction.getReactionCount())
                .build();
    }

    public Reaction toReaction(User user, Feed feed, long reactionCount) {
        return Reaction.builder()
                .user(user)
                .feed(feed)
                .reactionCount(reactionCount)
                .build();
    }
}
