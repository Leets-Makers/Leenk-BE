package leets.leenk.domain.feed.application.usecase;

import leets.leenk.domain.feed.application.dto.request.*;
import leets.leenk.domain.feed.application.dto.response.*;
import leets.leenk.domain.feed.application.exception.CommentDeleteNotAllowedException;
import leets.leenk.domain.feed.application.exception.FeedDeleteNotAllowedException;
import leets.leenk.domain.feed.application.exception.FeedUpdateNotAllowedException;
import leets.leenk.domain.feed.application.exception.SelfReactionNotAllowedException;
import leets.leenk.domain.feed.application.mapper.CommentMapper;
import leets.leenk.domain.feed.application.mapper.FeedMapper;
import leets.leenk.domain.feed.application.mapper.FeedUserMapper;
import leets.leenk.domain.feed.application.mapper.ReactionMapper;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.domain.service.*;
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.service.MediaDeleteService;
import leets.leenk.domain.media.domain.service.MediaGetService;
import leets.leenk.domain.media.domain.service.MediaSaveService;
import leets.leenk.domain.notification.application.usecase.FeedNotificationUsecase;
import leets.leenk.domain.feed.domain.event.FeedDomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import leets.leenk.domain.user.domain.service.NotionDatabaseService;
import leets.leenk.domain.user.domain.service.SlackWebhookService;
import leets.leenk.domain.user.domain.service.blockuser.UserBlockService;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedUsecase {

    private static final int MAX_NAVIGATION_SIZE = 3;
    private static final int DEFAULT_NAVIGATION_SIZE = 1;

    private final UserGetService userGetService;
    private final UserBlockService userBlockService;
    private final SlackWebhookService slackWebhookService;
    private final NotionDatabaseService notionDatabaseService;

    private final FeedGetService feedGetService;
    private final FeedSaveService feedSaveService;
    private final FeedUpdateService feedUpdateService;
    private final FeedDeleteService feedDeleteService;

    private final MediaGetService mediaGetService;
    private final MediaSaveService mediaSaveService;
    private final MediaDeleteService mediaDeleteService;

    private final LinkedUserGetService linkedUserGetService;
    private final LinkedUserSaveService linkedUserSaveService;
    private final LinkedUserDeleteService linkedUserDeleteService;

    private final ReactionGetService reactionGetService;
    private final ReactionSaveService reactionSaveService;

    private final CommentSaveService commentSaveService;
    private final CommentGetService commentGetService;
    private final CommentDeleteService commentDeleteService;

    private final FeedNotificationUsecase feedNotificationUsecase;
    private final ApplicationEventPublisher eventPublisher;

    private final FeedMapper feedMapper;
    private final MediaMapper mediaMapper;
    private final FeedUserMapper feedUserMapper;
    private final ReactionMapper reactionMapper;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public FeedListResponse getFeeds(long userId, int pageNumber, int pageSize) {
        User user = userGetService.findById(userId);
        List<UserBlock> blockedUsers = userBlockService.findAllByBlocker(user);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<Feed> slice = feedGetService.findAll(pageable, blockedUsers);

        List<Feed> feeds = slice.getContent();
        List<Media> medias = mediaGetService.findAllByFeeds(feeds);

        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        return feedMapper.toFeedListResponse(slice, mediaMap);
    }

    @Transactional(readOnly = true)
    public FeedDetailResponse getFeedDetail(Long feedId) {
        Feed feed = feedGetService.findById(feedId);
        List<Media> medias = mediaGetService.findAllByFeed(feed);
        List<LinkedUser> linkedUsers = linkedUserGetService.findAll(feed);
        List<Comment> comments = commentGetService.findAllByFeed(feed);

        return feedMapper.toFeedDetailResponse(feed, medias, linkedUsers, comments);
    }

    @Transactional(readOnly = true)
    public FeedNavigationResponse getFeedNavigation(
            Long feedId,
            Long currentUserId,
            Integer prevSize,
            Integer nextSize
    ) {
        // 파라미터 검증 및 기본값 설정
        int validatedPrevSize = validateSize(prevSize, DEFAULT_NAVIGATION_SIZE, MAX_NAVIGATION_SIZE);
        int validatedNextSize = validateSize(nextSize, DEFAULT_NAVIGATION_SIZE, MAX_NAVIGATION_SIZE);

        // 현재 피드 조회
        Feed currentFeed = feedGetService.findById(feedId);

        // 차단 사용자 목록 조회
        User currentUser = userGetService.findById(currentUserId);
        List<UserBlock> blockedUsers = userBlockService.findAllByBlocker(currentUser);

        // 이전/다음 피드 조회 (hasMore 정보 포함)
        FeedNavigationResult prevResult = feedGetService.findPrevFeedsWithHasMore(
                currentFeed, blockedUsers, validatedPrevSize
        );
        FeedNavigationResult nextResult = feedGetService.findNextFeedsWithHasMore(
                currentFeed, blockedUsers, validatedNextSize
        );

        List<Feed> prevFeeds = prevResult.feeds();
        List<Feed> nextFeeds = nextResult.feeds();
        boolean hasMorePrev = prevResult.hasMore();
        boolean hasMoreNext = nextResult.hasMore();

        // 모든 피드의 미디어와 링크된 사용자 조회
        List<Feed> allFeeds = new ArrayList<>();
        allFeeds.add(currentFeed);
        allFeeds.addAll(prevFeeds);
        allFeeds.addAll(nextFeeds);

        List<Media> allMedias = mediaGetService.findAllByFeeds(allFeeds);
        Map<Long, List<Media>> mediaMap = allMedias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        Map<Long, List<LinkedUser>> linkedUserMap = new HashMap<>();
        for (Feed feed : allFeeds) {
            List<LinkedUser> linkedUsers = linkedUserGetService.findAll(feed);
            linkedUserMap.put(feed.getId(), linkedUsers);
        }

        Map<Long, List<Comment>> commentsMap = new HashMap<>();
        for (Feed feed : allFeeds) {
            List<Comment> comments = commentGetService.findAllByFeed(feed);
            commentsMap.put(feed.getId(), comments);
        }

        return feedMapper.toFeedNavigationResponse(
                currentFeed,
                prevFeeds,
                nextFeeds,
                mediaMap,
                linkedUserMap,
                commentsMap,
                hasMorePrev,
                hasMoreNext
        );
    }

    private int validateSize(Integer size, int defaultValue, int maxValue) {
        if (size == null) {
            return defaultValue;
        }
        if (size < 0) {
            return 0;
        }
        if (size > maxValue) {
            return maxValue;
        }
        return size;
    }

    @Transactional
    public void uploadFeed(long userId, FeedUploadRequest request) {
        User author = userGetService.findById(userId);
        Feed feed = feedMapper.toFeed(author, request.description());
        feedSaveService.save(feed);

        List<Media> medias = request.media().stream()
                .map(mediaRequest -> mediaMapper.toMedia(feed, mediaRequest))
                .toList();
        mediaSaveService.saveAll(medias);

        List<LinkedUser> linkedUsers = getLinkedUsers(author, request.userIds(), feed);
        linkedUserSaveService.saveAll(linkedUsers);

        // TODO: 코틀린 마이그레이션 후 코틀린화하기
        eventPublisher.publishEvent(
                FeedDomainEvent.created(
                        feed.getId(),
                        author.getId(),
                        author.getName(),
                        linkedUsers.stream()
                                .map(LinkedUser::getUser)
                                .map(User::getId)
                                .toList()
                )
        );
    }

    private List<LinkedUser> getLinkedUsers(User author, List<Long> userIds, Feed feed) {
        Set<User> users = new HashSet<>(userGetService.findAll(userIds));
        users.add(author); // 중복 자동 제거

        return users.stream()
                .map(user -> feedUserMapper.toLinkedUser(user, feed))
                .toList();
    }

    /**
     * 피드에 공감을 추가
     * 비관적 락(PESSIMISTIC_WRITE)을 사용하여 동시성 문제를 해결
     * @see leets.leenk.domain.feed.domain.repository.FeedRepository#findByIdWithPessimisticLock
     * @see leets.leenk.domain.user.domain.repository.UserRepository#findByIdWithPessimisticLock
     */
    @Transactional
    public void reactToFeed(long userId, long feedId, ReactionRequest request) {
        Feed feed = feedGetService.findByIdWithLock(feedId); // !락 순서 중요!
        User user = userGetService.findById(userId);

        validateReaction(feed, user);

        Reaction reaction = reactionGetService.findByFeedAndUser(feed, user)
                .orElseGet(() ->
                        reactionSaveService.save(
                                reactionMapper.toReaction(user, feed, 0L)
                        )
                );

        Long previousReactionCount = reaction.getReactionCount();

        feedUpdateService.updateTotalReaction(feed, reaction, feed.getUser(), request.reactionCount());

        eventPublisher.publishEvent(
                FeedDomainEvent.Companion.reacted(
                    feed.getId(),
                    feed.getUser().getId(),
                    user.getId(),
                    user.getName(),
                    previousReactionCount,
                    feed.getTotalReactionCount()
            )
        );
    }

    @Transactional
    public void writeComment(long userId, long feedId, CommentWriteRequest request) {
        User user = userGetService.findById(userId);
        Feed feed = feedGetService.findById(feedId);

        Comment comment = commentMapper.toComment(user, feed, request);

        commentSaveService.saveComment(comment);
    }

    private void validateReaction(Feed feed, User user) {
        if (feed.getUser().equals(user)) {
            throw new SelfReactionNotAllowedException();
        }
    }

    @Transactional(readOnly = true)
    public List<ReactionUserResponse> getReactionUser(Long feedId) {
        Feed feed = feedGetService.findById(feedId);
        List<Reaction> reactions = reactionGetService.findAll(feed);

        return reactions.stream()
                .map(reactionMapper::toResponse)
                .toList();
    }

    @Transactional
    public void updateFeed(long userId, long feedId, FeedUpdateRequest request) {
        Feed feed = feedGetService.findById(feedId);
        User author = userGetService.findById(userId);

        checkAuthor(author, feed);

        feedUpdateService.update(feed, request);

        if (request.media() != null) {
            mediaDeleteService.deleteAllByFeed(feed);
            List<Media> newMedias = request.media().stream()
                    .map(mediaRequest -> mediaMapper.toMedia(feed, mediaRequest))
                    .toList();
            mediaSaveService.saveAll(newMedias);
        }

        if (request.userIds() != null) {
            linkedUserDeleteService.deleteAllByFeed(feed);
            List<LinkedUser> newLinkedUsers = getLinkedUsers(author, request.userIds(), feed);
            linkedUserSaveService.saveAll(newLinkedUsers);
        }
    }

    @Transactional(readOnly = true)
    public FeedListResponse getMyFeeds(long userId, int pageNumber, int pageSize) {
        return getFeedsByUser(userId, pageNumber, pageSize, true);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getOthersFeeds(long userId, int pageNumber, int pageSize) {
        return getFeedsByUser(userId, pageNumber, pageSize, false);
    }

    private FeedListResponse getFeedsByUser(long userId, int pageNumber, int pageSize, boolean includeTotalReaction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        User user = userGetService.findById(userId);

        Slice<Feed> slice = feedGetService.findAllByUser(user, pageable);
        List<Media> medias = mediaGetService.findAllByFeeds(slice.getContent());

        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        return feedMapper.toFeedListResponse(user, slice, mediaMap, includeTotalReaction);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getLinkedFeeds(long userId, int pageNumber, int pageSize) {
        User user = userGetService.findById(userId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Slice<Feed> slice = linkedUserGetService.findAllByUser(user, pageable);
        List<Media> medias = mediaGetService.findAllByFeeds(slice.getContent());

        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        return feedMapper.toFeedListResponse(slice, mediaMap);
    }

    @Transactional(readOnly = true)
    public List<FeedUserResponse> getAllUser() {
        List<User> users = userGetService.findAll();

        return users.stream()
                .map(feedUserMapper::toFeedUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedUserListResponse getUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<User> slice = userGetService.findAll(pageable);

        return feedUserMapper.toFeedUserListResponse(slice);
    }

    @Transactional
    public void deleteFeed(long userId, long feedId) {
        Feed feed = feedGetService.findById(feedId);
        User user = userGetService.findById(userId);

        if (!feed.getUser().equals(user)) {
            throw new FeedDeleteNotAllowedException();
        }

        feedDeleteService.delete(feed);
    }

    @Transactional
    public void deleteComment(long userId, long commentId) {
        User user = userGetService.findById(userId);
        Comment comment = commentGetService.findCommentByIdNotDeleted(commentId);

        if (!comment.getUser().equals(user)) {
            throw new CommentDeleteNotAllowedException();
        }

        commentDeleteService.deleteComment(comment);
    }

    @Transactional(readOnly = true)
    public void reportFeed(long userId, long feedId, FeedReportRequest request) {
        User user = userGetService.findById(userId);
        Feed feed = feedGetService.findById(feedId);

        notionDatabaseService.sendFeedReport(request.report(), user.getId(), feed.getId());
        slackWebhookService.sendFeedReport(request.report());
    }

    private void notifyIfReachedReactionMilestone(long previous, long current, Feed feed) {
        long[] milestones = {5, 10, 25, 50, 100, 250, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000};

        for (long milestone : milestones) {
            if (previous < milestone && current >= milestone) {
                feedNotificationUsecase.saveReactionCountNotification(feed, milestone);
            }
        }
    }

    private void checkAuthor(User user, Feed feed) {
        if (!feed.getUser().equals(user)) {
            throw new FeedUpdateNotAllowedException();
        }
    }
}
