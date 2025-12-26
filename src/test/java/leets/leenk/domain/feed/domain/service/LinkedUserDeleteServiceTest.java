package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class LinkedUserDeleteServiceTest {
    @Mock
    private LinkedUserRepository linkedUserRepository;

    @InjectMocks
    private LinkedUserDeleteService linkedUserDeleteService;

    @Test
    @DisplayName("deleteAllByFeed 테스트")
    void deleteAllByFeed() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);

        // when
        linkedUserDeleteService.deleteAllByFeed(feed);

        // then
        then(linkedUserRepository).should().deleteAllByFeed(feed);
        then(linkedUserRepository).should().flush();
    }
}
