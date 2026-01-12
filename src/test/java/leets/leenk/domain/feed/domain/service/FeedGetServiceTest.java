package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.exception.FeedNotFoundException;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.FeedRepository;
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static leets.leenk.domain.feed.test.FeedTestFixture.createFeedWithCreateDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class FeedGetServiceTest {
    @Mock
    private FeedRepository feedRepository;

    @InjectMocks
    private FeedGetService feedGetService;

    @Test
    @DisplayName("findById 테스트")
    void findById1() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);

        given(feedRepository.findByDeletedAtIsNullAndId(1L))
                .willReturn(Optional.of(feed));

        // when
        Feed result = feedGetService.findById(1L);

        // then
        assertThat(result).isSameAs(feed);
        then(feedRepository).should().findByDeletedAtIsNullAndId(1L);
    }

    @Test
    @DisplayName("findById 테스트 - 예외")
    void findById2() {
        // given
        given(feedRepository.findByDeletedAtIsNullAndId(99L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedGetService.findById(99L))
                .isInstanceOf(FeedNotFoundException.class);
        then(feedRepository).should().findByDeletedAtIsNullAndId(99L);
    }

    @Test
    @DisplayName("findAll 테스트")
    void findAll() {
        // given
        User blocker = UserTestFixture.createUser(1L, "blocker");
        User blocked1 = UserTestFixture.createUser(2L, "blocked1");
        User blocked2 = UserTestFixture.createUser(3L, "blocked2");
        List<UserBlock> blockedUsers = List.of(
                UserBlock.builder().blocker(blocker).blocked(blocked1).build(),
                UserBlock.builder().blocker(blocker).blocked(blocked2).build()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Slice<Feed> slice = new SliceImpl<>(List.of());

        given(feedRepository.findAllByDeletedAtIsNullWithUser(pageable, List.of(2L, 3L)))
                .willReturn(slice);

        // when
        Slice<Feed> result = feedGetService.findAll(pageable, blockedUsers);

        // then
        assertThat(result).isSameAs(slice);
        then(feedRepository).should().findAllByDeletedAtIsNullWithUser(pageable, List.of(2L, 3L));
    }

    @Test
    @DisplayName("findAllByUser 테스트")
    void findAllByUser() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Pageable pageable = PageRequest.of(0, 5);
        Slice<Feed> slice = new SliceImpl<>(List.of());

        given(feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable))
                .willReturn(slice);

        // when
        Slice<Feed> result = feedGetService.findAllByUser(user, pageable);

        // then
        assertThat(result).isSameAs(slice);
        then(feedRepository).should().findAllByUserAndDeletedAtIsNull(user, pageable);
    }

    @Test
    @DisplayName("findPrevFeedsWithHasMore 테스트")
    void findPrevFeedsWithHasMore() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);
        Feed current = createFeedWithCreateDate(100L, user, base);
        Feed first = createFeedWithCreateDate(1L, user, base.plusMinutes(1));
        Feed second = createFeedWithCreateDate(2L, user, base.plusMinutes(2));
        Feed third = createFeedWithCreateDate(3L, user, base.plusMinutes(3));

        given(feedRepository.findPrevFeeds(eq(base), isNull(), eq(PageRequest.of(0, 3))))
                .willReturn(new ArrayList<>(List.of(first, second, third)));

        // when
        FeedNavigationResult result = feedGetService.findPrevFeedsWithHasMore(current, List.of(), 2);

        // then
        assertThat(result.hasMore()).isTrue();
        assertThat(result.feeds()).containsExactly(second, first);
        then(feedRepository).should().findPrevFeeds(eq(base), isNull(), eq(PageRequest.of(0, 3)));
    }

    @Test
    @DisplayName("findNextFeedsWithHasMore 테스트")
    void findNextFeedsWithHasMore() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        User blocked = UserTestFixture.createUser(2L, "blocked");
        UserBlock block = UserBlock.builder().blocker(user).blocked(blocked).build();
        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);
        Feed current = createFeedWithCreateDate(100L, user, base);
        Feed first = createFeedWithCreateDate(1L, user, base.minusMinutes(1));
        Feed second = createFeedWithCreateDate(2L, user, base.minusMinutes(2));
        Feed third = createFeedWithCreateDate(3L, user, base.minusMinutes(3));

        given(feedRepository.findNextFeeds(eq(base), eq(List.of(2L)), eq(PageRequest.of(0, 3))))
                .willReturn(new ArrayList<>(List.of(first, second, third)));

        // when
        FeedNavigationResult result = feedGetService.findNextFeedsWithHasMore(current, List.of(block), 2);

        // then
        assertThat(result.hasMore()).isTrue();
        assertThat(result.feeds()).containsExactly(first, second);
        then(feedRepository).should().findNextFeeds(eq(base), eq(List.of(2L)), eq(PageRequest.of(0, 3)));
    }
}
