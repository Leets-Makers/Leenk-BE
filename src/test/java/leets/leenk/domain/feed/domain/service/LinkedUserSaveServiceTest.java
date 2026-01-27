package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.LinkedUserTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class LinkedUserSaveServiceTest {
    @Mock
    private LinkedUserRepository linkedUserRepository;

    @InjectMocks
    private LinkedUserSaveService linkedUserSaveService;

    @Test
    @DisplayName("linkedUserSave 테스트")
    void linkedUserSave() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        LinkedUser linkedUser = LinkedUserTestFixture.createLinkedUser(null, user, feed);

        // when
        linkedUserSaveService.save(linkedUser);

        // then
        then(linkedUserRepository).should().save(linkedUser);
    }

    @Test
    @DisplayName("saveAll 테스트")
    void saveAll() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        LinkedUser linkedUser = LinkedUserTestFixture.createLinkedUser(null, user, feed);
        List<LinkedUser> linkedUsers = List.of(linkedUser);

        // when
        linkedUserSaveService.saveAll(linkedUsers);

        // then
        then(linkedUserRepository).should().saveAll(linkedUsers);
    }
}
