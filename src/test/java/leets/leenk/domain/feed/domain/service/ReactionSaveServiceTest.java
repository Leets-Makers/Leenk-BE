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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ReactionSaveServiceTest {
    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionSaveService reactionSaveService;

    @Test
    @DisplayName("reactionSave 테스트")
    void reactionSave() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Reaction reaction = ReactionTestFixture.createReaction(feed, user, 1L);

        given(reactionRepository.save(reaction)).willReturn(reaction);

        // when
        Reaction result = reactionSaveService.save(reaction);

        // then
        assertThat(result).isSameAs(reaction);
        then(reactionRepository).should().save(reaction);
    }
}
