package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static leets.leenk.domain.feed.application.util.FeedDescriptionUtil.normalizeDescription;

@Service
public class FeedUpdateService {

    public void update(Feed feed, FeedUpdateRequest request) {
        String normalized = normalizeDescription(request.description());
        Optional.ofNullable(normalized)
                .ifPresent(feed::updateDescription);
    }

    public void updateTotalReaction(Feed feed, Reaction reaction, User user, long reactionCount) {
        feed.increaseTotalReactionCount(reactionCount);
        reaction.increaseReactionCount(reactionCount);
        user.increaseTotalReactionCount(reactionCount);
    }
}
