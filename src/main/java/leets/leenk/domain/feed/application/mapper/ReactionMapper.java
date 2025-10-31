package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {

    public ReactionUserResponse toResponse(Reaction reaction) {
        return ReactionUserResponse.builder()
                .userId(reaction.getUser().getId())
                .profileImage(reaction.getUser().getThumbnail())
                .name(reaction.getUser().getName())
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
