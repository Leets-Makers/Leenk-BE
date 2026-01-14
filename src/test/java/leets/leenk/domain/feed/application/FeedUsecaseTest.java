package leets.leenk.domain.feed.application;

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
import leets.leenk.domain.feed.application.usecase.FeedUsecase;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.domain.service.*;
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult;
import leets.leenk.domain.feed.test.*;
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest;
import leets.leenk.domain.media.application.mapper.MediaMapper;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.entity.enums.MediaType;
import leets.leenk.domain.media.domain.service.MediaDeleteService;
import leets.leenk.domain.media.domain.service.MediaGetService;
import leets.leenk.domain.media.domain.service.MediaSaveService;
import leets.leenk.domain.notification.application.usecase.FeedNotificationUsecase;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBlock;
import leets.leenk.domain.user.domain.service.NotionDatabaseService;
import leets.leenk.domain.user.domain.service.SlackWebhookService;
import leets.leenk.domain.user.domain.service.blockuser.UserBlockService;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedUsecaseTest {
    private static final Logger log = LoggerFactory.getLogger(FeedUsecaseTest.class);
    @Mock
    private UserGetService userGetService;
    @Mock
    private UserBlockService userBlockService;
    @Mock
    private SlackWebhookService slackWebhookService;
    @Mock
    private NotionDatabaseService notionDatabaseService;

    @Mock
    private FeedGetService feedGetService;
    @Mock
    private FeedSaveService feedSaveService;
    @Mock
    private FeedUpdateService feedUpdateService;
    @Mock
    private FeedDeleteService feedDeleteService;

    @Mock
    private MediaGetService mediaGetService;
    @Mock
    private MediaSaveService mediaSaveService;
    @Mock
    private MediaDeleteService mediaDeleteService;

    @Mock
    private LinkedUserGetService linkedUserGetService;
    @Mock
    private LinkedUserSaveService linkedUserSaveService;
    @Mock
    private LinkedUserDeleteService linkedUserDeleteService;

    @Mock
    private ReactionGetService reactionGetService;
    @Mock
    private ReactionSaveService reactionSaveService;

    @Mock
    private CommentSaveService commentSaveService;
    @Mock
    private CommentGetService commentGetService;
    @Mock
    private CommentDeleteService commentDeleteService;

    @Mock
    private FeedNotificationUsecase feedNotificationUsecase;

    @Mock
    private FeedMapper feedMapper;
    @Mock
    private MediaMapper mediaMapper;
    @Mock
    private FeedUserMapper feedUserMapper;
    @Mock
    private ReactionMapper reactionMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private FeedUsecase feedUseCase;

    @Test
    @DisplayName("차단유저 목록으로 피드 조회 후 미디어 맵핑하여 응답 반환")
    void getFeeds() {
        // given
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        User loginUser = UserTestFixture.createUser(userId, "me");
        given(userGetService.findById(userId)).willReturn(loginUser);

        UserBlock block = mock(UserBlock.class);
        given(userBlockService.findAllByBlocker(loginUser)).willReturn(List.of(block));

        User author = UserTestFixture.createUser(2L, "author");
        Feed feed1 = FeedTestFixture.createFeed(100L, author);

        Slice<Feed> slice = new SliceImpl<>(
                List.of(feed1),
                PageRequest.of(pageNumber, pageSize),
                false
        );
        given(feedGetService.findAll(any(Pageable.class), anyList())).willReturn(slice);

        Media media1 = MediaTestFixture.createMedia(1L, feed1);
        given(mediaGetService.findAllByFeeds(List.of(feed1))).willReturn(List.of(media1));

        FeedListResponse expected = mock(FeedListResponse.class);
        given(feedMapper.toFeedListResponse(eq(slice), anyMap())).willReturn(expected);

        // when
        FeedListResponse result = feedUseCase.getFeeds(userId, pageNumber, pageSize);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("피드, 미디어, 링크, 유저, 댓글 조회 후 상세응답 반환")
    void getFeedDetail() {
        // given
        long feedId = 10L;

        User author = UserTestFixture.createUser(2L, "author");
        Feed feed = FeedTestFixture.createFeed(10L, author);

        Media m1 = MediaTestFixture.createMedia(1L, feed);
        List<Media> medias = List.of(m1);

        List<LinkedUser> linkedUsers = List.of(mock(LinkedUser.class));
        List<Comment> comments = List.of(
                CommentTestFixture.createComment(1L, author, feed, "hi")
        );

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(mediaGetService.findAllByFeed(feed)).willReturn(medias);
        given(linkedUserGetService.findAll(feed)).willReturn(linkedUsers);
        given(commentGetService.findAllByFeed(feed)).willReturn(comments);

        FeedDetailResponse expected = mock(FeedDetailResponse.class);
        given(feedMapper.toFeedDetailResponse(feed, medias, linkedUsers, comments)).willReturn(expected);

        // when
        FeedDetailResponse result = feedUseCase.getFeedDetail(feedId);

        // then
        assertThat(result).isEqualTo(expected);
        then(feedGetService).should().findById(feedId);
        then(mediaGetService).should().findAllByFeed(feed);
        then(linkedUserGetService).should().findAll(feed);
        then(commentGetService).should().findAllByFeed(feed);
        then(feedMapper).should().toFeedDetailResponse(feed, medias, linkedUsers, comments);
    }

    @Test
    @DisplayName("prevNext 조회 후 모든 피드,미디어, 링크, 유저, 댓글을 조합하여 응답 반환")
    void getFeedNavigation1() {
        // given
        long feedId = 10L;
        long currentUserId = 1L;

        User currentUser = UserTestFixture.createUser(currentUserId, "me");
        given(userGetService.findById(currentUserId)).willReturn(currentUser);

        User author = UserTestFixture.createUser(2L, "author");
        Feed currentFeed = FeedTestFixture.createFeed(feedId, author);
        given(feedGetService.findById(feedId)).willReturn(currentFeed);

        List<UserBlock> blocked = List.of(mock(UserBlock.class));
        given(userBlockService.findAllByBlocker(currentUser)).willReturn(blocked);

        Feed prev1 = FeedTestFixture.createFeed(11L, author);
        Feed next1 = FeedTestFixture.createFeed(9L, author);

        given(feedGetService.findPrevFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)))
                .willReturn(new FeedNavigationResult(List.of(prev1), true));
        given(feedGetService.findNextFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)))
                .willReturn(new FeedNavigationResult(List.of(next1), false));

        List<Media> allMedias = List.of(
                MediaTestFixture.createMedia(1L, currentFeed),
                MediaTestFixture.createMedia(2L, prev1),
                MediaTestFixture.createMedia(3L, next1)
        );
        given(mediaGetService.findAllByFeeds(argThat(list -> list.size() == 3))).willReturn(allMedias);

        given(linkedUserGetService.findAll(any(Feed.class))).willReturn(List.of(mock(LinkedUser.class)));
        given(commentGetService.findAllByFeed(any(Feed.class))).willReturn(List.of(mock(Comment.class)));

        FeedNavigationResponse expected = mock(FeedNavigationResponse.class);
        given(feedMapper.toFeedNavigationResponse(
                eq(currentFeed),
                eq(List.of(prev1)),
                eq(List.of(next1)),
                anyMap(),
                anyMap(),
                anyMap(),
                eq(true),
                eq(false)
        )).willReturn(expected);

        // when
        FeedNavigationResponse result = feedUseCase.getFeedNavigation(feedId, currentUserId, 1, 1);

        // then
        assertThat(result).isEqualTo(expected);

        then(feedGetService).should().findById(feedId);
        then(userGetService).should().findById(currentUserId);
        then(userBlockService).should().findAllByBlocker(currentUser);

        then(feedGetService).should().findPrevFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1));
        then(feedGetService).should().findNextFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1));

        then(mediaGetService).should().findAllByFeeds(anyList());

        then(linkedUserGetService).should(times(3)).findAll(any(Feed.class));
        then(commentGetService).should(times(3)).findAllByFeed(any(Feed.class));

        then(feedMapper).should().toFeedNavigationResponse(
                eq(currentFeed),
                eq(List.of(prev1)),
                eq(List.of(next1)),
                anyMap(),
                anyMap(),
                anyMap(),
                eq(true),
                eq(false)
        );
    }

    @Test
    @DisplayName("prevNextSize가 null이면 default로_조회")
    void getFeedNavigation2() {
        // given
        long feedId = 10L;
        long currentUserId = 1L;

        User me = UserTestFixture.createUser(currentUserId, "me");
        given(userGetService.findById(currentUserId)).willReturn(me);

        User author = UserTestFixture.createUser(2L, "author");
        Feed current = FeedTestFixture.createFeed(feedId, author);
        given(feedGetService.findById(feedId)).willReturn(current);

        given(userBlockService.findAllByBlocker(me)).willReturn(List.of());

        given(feedGetService.findPrevFeedsWithHasMore(eq(current), anyList(), eq(1)))
                .willReturn(new FeedNavigationResult(List.of(), false));
        given(feedGetService.findNextFeedsWithHasMore(eq(current), anyList(), eq(1)))
                .willReturn(new FeedNavigationResult(List.of(), false));

        given(mediaGetService.findAllByFeeds(anyList())).willReturn(List.of());
        given(linkedUserGetService.findAll(any(Feed.class))).willReturn(List.of());
        given(commentGetService.findAllByFeed(any(Feed.class))).willReturn(List.of());

        FeedNavigationResponse expected = mock(FeedNavigationResponse.class);
        given(feedMapper.toFeedNavigationResponse(eq(current), eq(List.of()), eq(List.of()),
                anyMap(), anyMap(), anyMap(), eq(false), eq(false))).willReturn(expected);

        // when
        FeedNavigationResponse result = feedUseCase.getFeedNavigation(feedId, currentUserId, null, null);

        // then
        assertThat(result).isEqualTo(expected);
        then(feedGetService).should().findPrevFeedsWithHasMore(eq(current), anyList(), eq(1));
        then(feedGetService).should().findNextFeedsWithHasMore(eq(current), anyList(), eq(1));
    }

    @Test
    @DisplayName("피드저장, 미디어저장, 링크유저저장, 알림저장")
    void uploadFeed() {
        // given
        long userId = 1L;
        User author = UserTestFixture.createUser(userId, "me");
        given(userGetService.findById(userId)).willReturn(author);

        FeedUploadRequest request = new FeedUploadRequest(
                "desc",
                List.of(new FeedMediaRequest(1, "https://example.com/a.png", MediaType.IMAGE)),
                List.of(2L, 3L)
        );

        Feed feed = FeedTestFixture.createFeed(100L, author);
        given(feedMapper.toFeed(author, request.description())).willReturn(feed);

        given(mediaMapper.toMedia(eq(feed), any(FeedMediaRequest.class)))
                .willReturn(MediaTestFixture.createMedia(1L, feed));

        User u2 = UserTestFixture.createUser(2L, "u2");
        User u3 = UserTestFixture.createUser(3L, "u3");
        given(userGetService.findAll(request.userIds())).willReturn(List.of(u2, u3));

        given(feedUserMapper.toLinkedUser(any(User.class), eq(feed))).willReturn(mock(LinkedUser.class));

        // when
        feedUseCase.uploadFeed(userId, request);

        // then
        then(feedSaveService).should().save(feed);
        then(mediaSaveService).should().saveAll(anyList());
        then(linkedUserSaveService).should().saveAll(anyList());

        then(feedNotificationUsecase).should().saveNewFeedNotification(feed);
        then(feedNotificationUsecase).should().saveTagNotification(eq(feed), anyList(), eq(author));
    }

    @Test
    @DisplayName("자기 피드에 공감하면 예외 발생")
    void reactToFeed1() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User me = UserTestFixture.createUser(userId, "me");
        Feed myFeed = FeedTestFixture.createFeed(feedId, me);

        given(feedGetService.findByIdWithLock(feedId)).willReturn(myFeed);
        given(userGetService.findById(userId)).willReturn(me);
        given(userGetService.findByIdWithLock(userId)).willReturn(me);

        ReactionRequest request = new ReactionRequest(1L);

        // when & then
        assertThatThrownBy(() -> feedUseCase.reactToFeed(userId, feedId, request))
                .isInstanceOf(SelfReactionNotAllowedException.class);

        then(reactionGetService).shouldHaveNoInteractions();
        then(feedUpdateService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("기존 리액션이 있고 기준치 넘을 시 알림 저장")
    void reactToFeed2() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User me = UserTestFixture.createUser(userId, "me");
        User author = UserTestFixture.createUser(2L, "author");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findByIdWithLock(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(me);
        given(userGetService.findByIdWithLock(2L)).willReturn(author);

        Reaction reaction = ReactionTestFixture.createReaction(feed, me, 4);
        given(reactionGetService.findByFeedAndUser(feed, me)).willReturn(Optional.of(reaction));

        ReactionRequest request = new ReactionRequest(1L);

        // when
        feedUseCase.reactToFeed(userId, feedId, request);

        // then
        then(feedUpdateService).should().updateTotalReaction(feed, reaction, author, 1L);
        then(feedNotificationUsecase).should().saveFirstReactionNotification(reaction);
        then(feedNotificationUsecase).should().saveReactionCountNotification(feed, 5);
    }

    @Test
    @DisplayName("기존 리액션이 없으면 업데이트 후 첫 리액션 알림 저장")
    void reactToFeed3() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User me = UserTestFixture.createUser(userId, "me");
        User author = UserTestFixture.createUser(2L, "author");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findByIdWithLock(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(me);
        given(userGetService.findByIdWithLock(2L)).willReturn(author);

        given(reactionGetService.findByFeedAndUser(feed, me)).willReturn(Optional.empty());

        Reaction toSave = ReactionTestFixture.createReaction(feed, me, 0);
        Reaction saved = ReactionTestFixture.createReaction(feed, me, 0);

        given(reactionMapper.toReaction(me, feed, 0L)).willReturn(toSave);
        given(reactionSaveService.save(toSave)).willReturn(saved);

        ReactionRequest request = new ReactionRequest(1L);

        // when
        feedUseCase.reactToFeed(userId, feedId, request);

        // then
        then(reactionSaveService).should().save(toSave);
        then(feedUpdateService).should().updateTotalReaction(feed, saved, author, 1L);
        then(feedNotificationUsecase).should().saveFirstReactionNotification(saved);
        then(feedNotificationUsecase).should(never()).saveReactionCountNotification(any(), anyLong());
    }

    @Test
    @DisplayName("댓글 생성 후 저장")
    void writeComment() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User me = UserTestFixture.createUser(userId, "me");
        User author = UserTestFixture.createUser(2L, "author");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(userGetService.findById(userId)).willReturn(me);
        given(feedGetService.findById(feedId)).willReturn(feed);

        CommentWriteRequest request = new CommentWriteRequest("hello");
        Comment comment = CommentTestFixture.createComment(1L, me, feed, "hello");
        given(commentMapper.toComment(me, feed, request)).willReturn(comment);

        // when
        feedUseCase.writeComment(userId, feedId, request);

        // then
        then(commentSaveService).should().saveComment(comment);
    }

    @Test
    @DisplayName("리액션 목록 조회 후 응답 반환")
    void getReactionUser1() {
        // given
        long feedId = 10L;

        User author = UserTestFixture.createUser(2L, "author");
        User u1 = UserTestFixture.createUser(1L, "u1");
        User u2 = UserTestFixture.createUser(3L, "u2");

        Feed feed = FeedTestFixture.createFeed(feedId, author);

        Reaction r1 = ReactionTestFixture.createReaction(feed, u1, 5);
        Reaction r2 = ReactionTestFixture.createReaction(feed, u2, 1);

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(reactionGetService.findAll(feed)).willReturn(List.of(r1, r2));

        ReactionUserResponse resp1 = mock(ReactionUserResponse.class);
        ReactionUserResponse resp2 = mock(ReactionUserResponse.class);

        given(reactionMapper.toResponse(r1)).willReturn(resp1);
        given(reactionMapper.toResponse(r2)).willReturn(resp2);

        // when
        List<ReactionUserResponse> result = feedUseCase.getReactionUser(feedId);

        // then
        assertThat(result).containsExactly(resp1, resp2);
        then(reactionMapper).should(times(2)).toResponse(any(Reaction.class));
    }

    @Test
    @DisplayName("작성자가 아니면 예외발생")
    void updateFeed1() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User author = UserTestFixture.createUser(2L, "author");
        User other = UserTestFixture.createUser(userId, "other");

        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(other);

        FeedUpdateRequest request = new FeedUpdateRequest("hello", null, null);

        // when & then
        assertThatThrownBy(() -> feedUseCase.updateFeed(userId, feedId, request))
                .isInstanceOf(FeedUpdateNotAllowedException.class);

        then(feedUpdateService).shouldHaveNoInteractions();
        then(mediaDeleteService).shouldHaveNoInteractions();
        then(linkedUserDeleteService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("미디어와 링크 유저 목록이 있으면 기존 삭제 후 새로 저장")
    void updateFeed2() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User author = UserTestFixture.createUser(userId, "author");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(author);

        FeedMediaRequest mr = new FeedMediaRequest(1, "https://example.com/a.jpg", MediaType.IMAGE);
        FeedUpdateRequest request = new FeedUpdateRequest("new", List.of(mr), List.of(2L));

        given(mediaMapper.toMedia(eq(feed), eq(mr))).willReturn(MediaTestFixture.createMedia(1L, feed));

        User u2 = UserTestFixture.createUser(2L, "u2");
        given(userGetService.findAll(request.userIds())).willReturn(List.of(u2));
        given(feedUserMapper.toLinkedUser(any(User.class), eq(feed))).willReturn(mock(LinkedUser.class));

        // when
        feedUseCase.updateFeed(userId, feedId, request);

        // then
        then(feedUpdateService).should().update(feed, request);

        then(mediaDeleteService).should().deleteAllByFeed(feed);
        then(mediaSaveService).should().saveAll(anyList());

        then(linkedUserDeleteService).should().deleteAllByFeed(feed);
        then(linkedUserSaveService).should().saveAll(anyList());
    }

    @Test
    @DisplayName("내 Feeds 응답 매핑")
    void getMyFeeds() {
        // given
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        User user = UserTestFixture.createUser(userId, "me");
        given(userGetService.findById(userId)).willReturn(user);

        Feed f1 = FeedTestFixture.createFeed(100L, user);

        Slice<Feed> slice = new SliceImpl<>(List.of(f1), PageRequest.of(pageNumber, pageSize), false);
        given(feedGetService.findAllByUser(eq(user), any(Pageable.class))).willReturn(slice);

        given(mediaGetService.findAllByFeeds(slice.getContent()))
                .willReturn(List.of(MediaTestFixture.createMedia(1L, f1)));

        FeedListResponse expected = mock(FeedListResponse.class);
        given(feedMapper.toFeedListResponse(eq(user), eq(slice), anyMap(), eq(true))).willReturn(expected);

        // when
        FeedListResponse result = feedUseCase.getMyFeeds(userId, pageNumber, pageSize);

        // then
        assertThat(result).isEqualTo(expected);
        then(feedMapper).should().toFeedListResponse(eq(user), eq(slice), anyMap(), eq(true));
    }

    @Test
    @DisplayName(("다른 사람들 Feeds 응답 매핑"))
    void getOtherFeeds() {
        // given
        long otherUserId = 2L;
        int pageNumber = 0;
        int pageSize = 10;

        User other = UserTestFixture.createUser(otherUserId, "other");
        given(userGetService.findById(otherUserId)).willReturn(other);

        Feed f1 = FeedTestFixture.createFeed(100L, other);

        Slice<Feed> slice = new SliceImpl<>(List.of(f1), PageRequest.of(pageNumber, pageSize), false);
        given(feedGetService.findAllByUser(eq(other), any(Pageable.class))).willReturn(slice);

        given(mediaGetService.findAllByFeeds(slice.getContent()))
                .willReturn(List.of(MediaTestFixture.createMedia(1L, f1)));

        FeedListResponse expected = mock(FeedListResponse.class);
        given(feedMapper.toFeedListResponse(eq(other), eq(slice), anyMap(), eq(false))).willReturn(expected);

        // when
        FeedListResponse result = feedUseCase.getOthersFeeds(otherUserId, pageNumber, pageSize);

        // then
        assertThat(result).isEqualTo(expected);
        then(feedMapper).should().toFeedListResponse(eq(other), eq(slice), anyMap(), eq(false));
    }

    @Test
    @DisplayName("함깨한 피드 조회 후 매핑하여 응답 변환")
    void getLinkedFeeds() {
        // given
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        User user = UserTestFixture.createUser(userId, "me");
        given(userGetService.findById(userId)).willReturn(user);

        User author = UserTestFixture.createUser(2L, "author");
        Feed f1 = FeedTestFixture.createFeed(100L, author);

        Slice<Feed> slice = new SliceImpl<>(List.of(f1), PageRequest.of(pageNumber, pageSize), false);
        given(linkedUserGetService.findAllByUser(eq(user), any(Pageable.class))).willReturn(slice);

        given(mediaGetService.findAllByFeeds(slice.getContent()))
                .willReturn(List.of(MediaTestFixture.createMedia(1L, f1)));

        FeedListResponse expected = mock(FeedListResponse.class);
        given(feedMapper.toFeedListResponse(eq(slice), anyMap())).willReturn(expected);

        // when
        FeedListResponse result = feedUseCase.getLinkedFeeds(userId, pageNumber, pageSize);

        //then
        assertThat(result).isEqualTo(expected);
        then(feedMapper).should().toFeedListResponse(eq(slice), anyMap());
    }

    @Test
    @DisplayName("전체 유저 조회 후 응답 매핑")
    void getAllUser() {
        // given
        User u1 = UserTestFixture.createUser(1L, "me");
        User u2 = UserTestFixture.createUser(2L, "me2");
        given(userGetService.findAll()).willReturn(List.of(u1, u2));

        FeedUserResponse r1 = mock(FeedUserResponse.class);
        FeedUserResponse r2 = mock(FeedUserResponse.class);
        given(feedUserMapper.toFeedUserResponse(eq(u1))).willReturn(r1);
        given(feedUserMapper.toFeedUserResponse(eq(u2))).willReturn(r2);

        // when
        List<FeedUserResponse> result = feedUseCase.getAllUser();

        // then
        assertThat(result).containsExactly(r1, r2);
        then(feedUserMapper).should(times(2)).toFeedUserResponse(any(User.class));
    }

    @Test
    @DisplayName("페이지로 유저 조회 후 응답 매핑")
    void getUsers() {
        // given
        int pageNumber = 0;
        int pageSize = 10;

        User u1 = UserTestFixture.createUser(1L, "me");
        Slice<User> slice = new SliceImpl<>(List.of(u1), PageRequest.of(pageNumber, pageSize), true);
        given(userGetService.findAll(any(Pageable.class))).willReturn(slice);

        FeedUserListResponse expected = mock(FeedUserListResponse.class);
        given(feedUserMapper.toFeedUserListResponse(slice)).willReturn(expected);

        // when
        FeedUserListResponse result = feedUseCase.getUsers(pageNumber, pageSize);

        // then
        assertThat(result).isEqualTo(expected);
        then(feedUserMapper).should().toFeedUserListResponse(slice);
    }

    @Test
    @DisplayName("피드 작성자면 피드 삭제")
    void deleteFeed1() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User author = UserTestFixture.createUser(userId, "me");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(author);

        // when
        feedUseCase.deleteFeed(userId, feedId);

        //then
        then(feedDeleteService).should().delete(feed);
    }

    @Test
    @DisplayName("피드 작성자가 아니면 예외 처리")
    void deleteFeed2() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User author = UserTestFixture.createUser(2L, "me");
        User other = UserTestFixture.createUser(userId, "me2");

        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(feedGetService.findById(feedId)).willReturn(feed);
        given(userGetService.findById(userId)).willReturn(other);

        // when & then
        assertThatThrownBy(() -> feedUseCase.deleteFeed(userId, feedId))
                .isInstanceOf(FeedDeleteNotAllowedException.class);

        then(feedDeleteService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 작성자면 댓글 삭제")
    void deleteComment() {
        // given
        long userId = 1L;
        long commentId = 5L;

        User author = UserTestFixture.createUser(2L, "me");
        User me = UserTestFixture.createUser(userId, "me2");
        Feed feed = FeedTestFixture.createFeed(10L, author);

        Comment comment = CommentTestFixture.createComment(commentId, me, feed, "wq");
        given(userGetService.findById(userId)).willReturn(me);
        given(commentGetService.findCommentByIdNotDeleted(commentId)).willReturn(comment);

        // when
        feedUseCase.deleteComment(userId, commentId);

        //then
        then(commentDeleteService).should().deleteComment(comment);
    }

    @Test
    @DisplayName("댓글 작성자가 아니면 예외처리")
    void deleteComment2() {
        // given
        long userId = 1L;
        long commentId = 5L;

        User author = UserTestFixture.createUser(2L, "me");
        User me = UserTestFixture.createUser(userId, "me2");
        User other = UserTestFixture.createUser(3L, "me3");
        Feed feed = FeedTestFixture.createFeed(10L, author);

        Comment comment = CommentTestFixture.createComment(commentId, other, feed, "wq");
        given(userGetService.findById(userId)).willReturn(me);
        given(commentGetService.findCommentByIdNotDeleted(commentId)).willReturn(comment);

        // when & then
        assertThatThrownBy(() -> feedUseCase.deleteComment(userId, commentId))
                .isInstanceOf(CommentDeleteNotAllowedException.class);

        then(commentDeleteService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("노션과 슬랙으로 신고 내용 전송")
    void reportFeed() {
        // given
        long userId = 1L;
        long feedId = 10L;

        User me = UserTestFixture.createUser(userId, "me");
        User author = UserTestFixture.createUser(2L, "me");
        Feed feed = FeedTestFixture.createFeed(feedId, author);

        given(userGetService.findById(userId)).willReturn(me);
        given(feedGetService.findById(feedId)).willReturn(feed);

        FeedReportRequest request = new FeedReportRequest("신고");

        // when
        feedUseCase.reportFeed(userId, feedId, request);

        // then
        then(notionDatabaseService).should().sendFeedReport(request.report(), me.getId(), feed.getId());
        then(slackWebhookService).should().sendFeedReport(request.report());
    }
}
