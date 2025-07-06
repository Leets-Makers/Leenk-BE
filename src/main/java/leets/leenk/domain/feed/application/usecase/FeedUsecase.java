package leets.leenk.domain.feed.application.usecase;

import leets.leenk.domain.feed.application.dto.request.FeedReportRequest;
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest;
import leets.leenk.domain.feed.application.dto.request.FeedUploadRequest;
import leets.leenk.domain.feed.application.dto.request.ReactionRequest;
import leets.leenk.domain.feed.application.dto.response.*;
import leets.leenk.domain.feed.application.exception.FeedDeleteNotAllowedException;
import leets.leenk.domain.feed.application.exception.SelfReactionNotAllowedException;
import leets.leenk.domain.feed.application.mapper.FeedMapper;
import leets.leenk.domain.feed.application.mapper.FeedUserMapper;
import leets.leenk.domain.feed.application.mapper.ReactionMapper;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.domain.service.*;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.service.MediaGetService;
import leets.leenk.domain.media.domain.service.MediaSaveService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedUsecase {

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

    private final LinkedUserGetService linkedUserGetService;
    private final LinkedUserSaveService linkedUserSaveService;

    private final ReactionGetService reactionGetService;
    private final ReactionSaveService reactionSaveService;

    private final FeedMapper feedMapper;
    private final MediaMapper mediaMapper;
    private final FeedUserMapper feedUserMapper;
    private final ReactionMapper reactionMapper;

    @Transactional(readOnly = true)
    public FeedListResponse getFeeds(long userId, int pageNumber, int pageSize) {
        User user = userGetService.findById(userId);
        List<UserBlock> blockedUsers = userBlockService.findAllByBlocker(user);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<Feed> slice = feedGetService.findAll(pageable, blockedUsers);

        List<Feed> feeds = slice.getContent();
        List<Media> medias = mediaGetService.findAll(feeds);

        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        return feedMapper.toFeedListResponse(slice, mediaMap);
    }

    @Transactional(readOnly = true)
    public FeedDetailResponse getFeedDetail(Long feedId) {
        Feed feed = feedGetService.findById(feedId);
        List<Media> medias = mediaGetService.findAll(feed);
        List<LinkedUser> linkedUsers = linkedUserGetService.findAll(feed);

        return feedMapper.toFeedDetailResponse(feed, medias, linkedUsers);
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

        List<LinkedUser> linkedUsers = getLinkedUsers(author, request.userId(), feed);
        linkedUserSaveService.saveAll(linkedUsers);
    }

    private List<LinkedUser> getLinkedUsers(User author, List<Long> userIds, Feed feed) {
        Set<User> users = new HashSet<>(userGetService.findAll(userIds));
        users.add(author); // 중복 자동 제거

        return users.stream()
                .map(user -> feedUserMapper.toLinkedUser(user, feed))
                .toList();
    }

    @Transactional
    public void reactToFeed(long userId, long feedId, ReactionRequest request) {
        User user = userGetService.findById(userId);
        Feed feed = feedGetService.findById(feedId);
        validateReaction(feed, user);

        Reaction reaction = reactionGetService.findByFeedAndUser(feed, user)
                .orElseGet(() ->
                        reactionSaveService.save(
                                reactionMapper.toReaction(user, feed, 0L)
                        )
                );

        feedUpdateService.updateTotalReaction(feed, reaction, feed.getUser(), request.reactionCount());
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

    public void updateFeed(FeedUpdateRequest request) {
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
        List<Media> medias = mediaGetService.findAll(slice.getContent());

        Map<Long, List<Media>> mediaMap = medias.stream()
                .collect(Collectors.groupingBy(media -> media.getFeed().getId()));

        return feedMapper.toFeedListResponse(user, slice, mediaMap, includeTotalReaction);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getLinkedFeeds(long userId, int pageNumber, int pageSize) {
        User user = userGetService.findById(userId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Slice<Feed> slice = linkedUserGetService.findAllByUser(user, pageable);
        List<Media> medias = mediaGetService.findAll(slice.getContent());

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

    @Transactional(readOnly = true)
    public void reportFeed(long userId, long feedId, FeedReportRequest request) {
        User user = userGetService.findById(userId);
        Feed feed = feedGetService.findById(feedId);

        notionDatabaseService.sendFeedReport(request.report(), user.getId(), feed.getId());
        slackWebhookService.sendFeedReport(request.report());
    }
}
