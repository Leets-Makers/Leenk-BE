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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class LinkedUserGetServiceTest {
    @Mock
    private LinkedUserRepository linkedUserRepository;

    @InjectMocks
    private LinkedUserGetService linkedUserGetService;

    @Test
    @DisplayName("findAll 테스트")
    void findAll() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        LinkedUser linkedUser = LinkedUserTestFixture.createLinkedUser(1L, user, feed);
        List<LinkedUser> linkedUsers = List.of(linkedUser);

        given(linkedUserRepository.findAllByFeed(feed)).willReturn(linkedUsers);

        // when
        List<LinkedUser> result = linkedUserGetService.findAll(feed);

        // then
        assertThat(result).isSameAs(linkedUsers);
        then(linkedUserRepository).should().findAllByFeed(feed);
    }

    @Test
    @DisplayName("findAllByUser 테스트")
    void findAllByUser() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Pageable pageable = PageRequest.of(0, 5);
        Slice<Feed> slice = new SliceImpl<>(List.of());

        given(linkedUserRepository.findFeedsByLinkedUser(user, pageable))
                .willReturn(slice);

        // when
        Slice<Feed> result = linkedUserGetService.findAllByUser(user, pageable);

        // then
        assertThat(result).isSameAs(slice);
        then(linkedUserRepository).should().findFeedsByLinkedUser(user, pageable);
    }
}
