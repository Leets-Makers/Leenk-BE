package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.domain.repository.ReactionRepository;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.ReactionTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ReactionGetServiceTest {
    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionGetService reactionGetService;

    @Test
    @DisplayName("findByFeedAndUser 테스트")
    void findByFeedAndUser() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Reaction reaction = ReactionTestFixture.createReaction(feed, user, 1L);

        given(reactionRepository.findByFeedAndUser(feed, user))
                .willReturn(Optional.of(reaction));

        // when
        Optional<Reaction> result = reactionGetService.findByFeedAndUser(feed, user);

        // then
        assertThat(result).containsSame(reaction);
        then(reactionRepository).should().findByFeedAndUser(feed, user);
    }

    @Test
    @DisplayName("findAll 테스트")
    void findAll() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Reaction reaction = ReactionTestFixture.createReaction(feed, user, 2L);
        List<Reaction> reactions = List.of(reaction);

        given(reactionRepository.findAllByFeed(feed)).willReturn(reactions);

        // when
        List<Reaction> result = reactionGetService.findAll(feed);

        // then
        assertThat(result).isSameAs(reactions);
        then(reactionRepository).should().findAllByFeed(feed);
    }
}
