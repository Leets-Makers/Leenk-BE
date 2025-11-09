package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.exception.FeedNotFoundException;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.FeedRepository;
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedGetService {

    private final FeedRepository feedRepository;

    public Feed findById(long feedId) {
        return feedRepository.findByDeletedAtIsNullAndId(feedId)
                .orElseThrow(FeedNotFoundException::new);
    }

    public Slice<Feed> findAll(Pageable pageable, List<UserBlock> blockedUser) {
        List<Long> blockedUserIds = blockedUser.stream()
                .map(UserBlock::getBlocked)
                .map(User::getId)
                .toList();

        return feedRepository.findAllByDeletedAtIsNullWithUser(pageable, blockedUserIds);
    }

    public Slice<Feed> findAllByUser(User user, Pageable pageable) {
        return feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable);
    }

    /**
     * 이전 피드 조회 (더 최신) - hasMore 정보 포함
     * ASC로 조회한 결과를 최신순(DESC)으로 역정렬하여 반환
     * 한 번의 쿼리로 피드 목록과 추가 피드 존재 여부를 함께 반환
     */
    public FeedNavigationResult findPrevFeedsWithHasMore(Feed currentFeed, List<UserBlock> blockedUsers, int size) {
        List<Long> blockedUserIds = blockedUsers.stream()
                .map(UserBlock::getBlocked)
                .map(User::getId)
                .toList();

        // size+1 개를 조회하여 hasMore 판단
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Feed> feeds = feedRepository.findPrevFeeds(
                currentFeed.getCreateDate(),
                blockedUserIds.isEmpty() ? null : blockedUserIds,
                pageable
        );

        // hasMore 판단
        boolean hasMore = feeds.size() > size;

        // size보다 많으면 잘라내기
        if (hasMore) {
            feeds = feeds.subList(0, size);
        }

        // ASC로 조회했으므로 최신순으로 역정렬
        Collections.reverse(feeds);

        return new FeedNavigationResult(feeds, hasMore);
    }

    /**
     * 다음 피드 조회 (더 오래된) - hasMore 정보 포함
     * DESC로 조회하므로 그대로 반환
     * 한 번의 쿼리로 피드 목록과 추가 피드 존재 여부를 함께 반환
     */
    public FeedNavigationResult findNextFeedsWithHasMore(Feed currentFeed, List<UserBlock> blockedUsers, int size) {
        List<Long> blockedUserIds = blockedUsers.stream()
                .map(UserBlock::getBlocked)
                .map(User::getId)
                .toList();

        // size+1 개를 조회하여 hasMore 판단
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Feed> feeds = feedRepository.findNextFeeds(
                currentFeed.getCreateDate(),
                blockedUserIds.isEmpty() ? null : blockedUserIds,
                pageable
        );

        // hasMore 판단
        boolean hasMore = feeds.size() > size;

        // size보다 많으면 잘라내기
        if (hasMore) {
            feeds = feeds.subList(0, size);
        }

        return new FeedNavigationResult(feeds, hasMore);
    }
}
