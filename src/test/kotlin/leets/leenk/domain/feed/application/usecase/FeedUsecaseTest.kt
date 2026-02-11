package leets.leenk.domain.feed.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.application.dto.request.CommentWriteRequest
import leets.leenk.domain.feed.application.dto.request.FeedReportRequest
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest
import leets.leenk.domain.feed.application.dto.request.FeedUploadRequest
import leets.leenk.domain.feed.application.dto.request.ReactionRequest
import leets.leenk.domain.feed.application.dto.response.FeedDetailResponse
import leets.leenk.domain.feed.application.dto.response.FeedListResponse
import leets.leenk.domain.feed.application.dto.response.FeedNavigationResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse
import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse
import leets.leenk.domain.feed.application.exception.CommentDeleteNotAllowedException
import leets.leenk.domain.feed.application.exception.FeedDeleteNotAllowedException
import leets.leenk.domain.feed.application.exception.FeedUpdateNotAllowedException
import leets.leenk.domain.feed.application.exception.SelfReactionNotAllowedException
import leets.leenk.domain.feed.application.mapper.CommentMapper
import leets.leenk.domain.feed.application.mapper.FeedMapper
import leets.leenk.domain.feed.application.mapper.FeedUserMapper
import leets.leenk.domain.feed.application.mapper.ReactionMapper
import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.feed.domain.service.CommentDeleteService
import leets.leenk.domain.feed.domain.service.CommentGetService
import leets.leenk.domain.feed.domain.service.CommentSaveService
import leets.leenk.domain.feed.domain.service.FeedDeleteService
import leets.leenk.domain.feed.domain.service.FeedGetService
import leets.leenk.domain.feed.domain.service.FeedSaveService
import leets.leenk.domain.feed.domain.service.FeedUpdateService
import leets.leenk.domain.feed.domain.service.LinkedUserDeleteService
import leets.leenk.domain.feed.domain.service.LinkedUserGetService
import leets.leenk.domain.feed.domain.service.LinkedUserSaveService
import leets.leenk.domain.feed.domain.service.ReactionGetService
import leets.leenk.domain.feed.domain.service.ReactionSaveService
import leets.leenk.domain.feed.domain.service.dto.FeedNavigationResult
import leets.leenk.domain.feed.test.CommentTestFixture
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.MediaTestFixture
import leets.leenk.domain.feed.test.ReactionTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest
import leets.leenk.domain.media.application.mapper.MediaMapper
import leets.leenk.domain.media.domain.entity.enums.MediaType
import leets.leenk.domain.media.domain.service.MediaDeleteService
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaSaveService
import leets.leenk.domain.notification.application.usecase.FeedNotificationUsecase
import leets.leenk.domain.user.domain.entity.UserBlock
import leets.leenk.domain.user.domain.service.NotionDatabaseService
import leets.leenk.domain.user.domain.service.SlackWebhookService
import leets.leenk.domain.user.domain.service.blockuser.UserBlockService
import leets.leenk.domain.user.domain.service.user.UserGetService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import java.util.Optional

class FeedUsecaseTest :
    BehaviorSpec({
        val userGetService = mockk<UserGetService>()
        val userBlockService = mockk<UserBlockService>()
        val slackWebhookService = mockk<SlackWebhookService>(relaxed = true)
        val notionDatabaseService = mockk<NotionDatabaseService>(relaxed = true)

        val feedGetService = mockk<FeedGetService>()
        val feedSaveService = mockk<FeedSaveService>(relaxed = true)
        val feedUpdateService = mockk<FeedUpdateService>(relaxed = true)
        val feedDeleteService = mockk<FeedDeleteService>(relaxed = true)

        val mediaGetService = mockk<MediaGetService>()
        val mediaSaveService = mockk<MediaSaveService>(relaxed = true)
        val mediaDeleteService = mockk<MediaDeleteService>(relaxed = true)

        val linkedUserGetService = mockk<LinkedUserGetService>()
        val linkedUserSaveService = mockk<LinkedUserSaveService>(relaxed = true)
        val linkedUserDeleteService = mockk<LinkedUserDeleteService>(relaxed = true)

        val reactionGetService = mockk<ReactionGetService>()
        val reactionSaveService = mockk<ReactionSaveService>()

        val commentSaveService = mockk<CommentSaveService>(relaxed = true)
        val commentGetService = mockk<CommentGetService>()
        val commentDeleteService = mockk<CommentDeleteService>(relaxed = true)

        val feedNotificationUsecase = mockk<FeedNotificationUsecase>(relaxed = true)

        val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

        val feedMapper = mockk<FeedMapper>()
        val mediaMapper = mockk<MediaMapper>()
        val feedUserMapper = mockk<FeedUserMapper>()
        val reactionMapper = mockk<ReactionMapper>()
        val commentMapper = mockk<CommentMapper>()

        val feedUsecase =
            FeedUsecase(
                userGetService,
                userBlockService,
                slackWebhookService,
                notionDatabaseService,
                feedGetService,
                feedSaveService,
                feedUpdateService,
                feedDeleteService,
                mediaGetService,
                mediaSaveService,
                mediaDeleteService,
                linkedUserGetService,
                linkedUserSaveService,
                linkedUserDeleteService,
                reactionGetService,
                reactionSaveService,
                commentSaveService,
                commentGetService,
                commentDeleteService,
                feedNotificationUsecase,
                feedMapper,
                mediaMapper,
                feedUserMapper,
                reactionMapper,
                commentMapper,
                eventPublisher,
            )

        Given("차단된 사용자 목록이 있을 때") {
            val userId = 1L
            val pageNumber = 0
            val pageSize = 10

            val loginUser = UserTestFixture.createUser(userId, "me")
            every { userGetService.findById(userId) } returns loginUser

            val block = mockk<UserBlock>()
            every { userBlockService.findAllByBlocker(loginUser) } returns listOf(block)

            val author = UserTestFixture.createUser(2L, "author")
            val feed1 = FeedTestFixture.createFeed(100L, author)

            val slice = SliceImpl(listOf(feed1), PageRequest.of(pageNumber, pageSize), false)
            every { feedGetService.findAll(any(), any()) } returns slice

            val media1 = MediaTestFixture.createMedia(1L, feed1)
            every { mediaGetService.findAllByFeeds(listOf(feed1)) } returns listOf(media1)

            val expected = mockk<FeedListResponse>()
            every { feedMapper.toFeedListResponse(eq(slice), any()) } returns expected

            When("피드 목록을 조회하면") {
                val result = feedUsecase.getFeeds(userId, pageNumber, pageSize)

                Then("차단된 사용자를 제외한 피드가 반환되어야 한다") {
                    result shouldBe expected
                }
            }
        }

        Given("피드 ID가 주어졌을 때") {
            val feedId = 10L

            val author = UserTestFixture.createUser(2L, "author")
            val feed = FeedTestFixture.createFeed(10L, author)

            val m1 = MediaTestFixture.createMedia(1L, feed)
            val medias = listOf(m1)

            val linkedUsers = listOf(mockk<LinkedUser>())
            val comments = listOf(CommentTestFixture.createComment(1L, author, feed, "hi"))

            every { feedGetService.findById(feedId) } returns feed
            every { mediaGetService.findAllByFeed(feed) } returns medias
            every { linkedUserGetService.findAll(feed) } returns linkedUsers
            every { commentGetService.findAllByFeed(feed) } returns comments

            val expected = mockk<FeedDetailResponse>()
            every { feedMapper.toFeedDetailResponse(feed, medias, linkedUsers, comments) } returns expected

            When("피드 상세를 조회하면") {
                val result = feedUsecase.getFeedDetail(feedId)

                Then("피드 상세 정보가 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedGetService.findById(feedId) }
                    verify { mediaGetService.findAllByFeed(feed) }
                    verify { linkedUserGetService.findAll(feed) }
                    verify { commentGetService.findAllByFeed(feed) }
                    verify { feedMapper.toFeedDetailResponse(feed, medias, linkedUsers, comments) }
                }
            }
        }

        Given("피드 네비게이션을 위한 이전/다음 피드가 있을 때") {
            val feedId = 10L
            val currentUserId = 1L

            val currentUser = UserTestFixture.createUser(currentUserId, "me")
            every { userGetService.findById(currentUserId) } returns currentUser

            val author = UserTestFixture.createUser(2L, "author")
            val currentFeed = FeedTestFixture.createFeed(feedId, author)
            every { feedGetService.findById(feedId) } returns currentFeed

            val blocked = listOf(mockk<UserBlock>())
            every { userBlockService.findAllByBlocker(currentUser) } returns blocked

            val prev1 = FeedTestFixture.createFeed(11L, author)
            val next1 = FeedTestFixture.createFeed(9L, author)

            every { feedGetService.findPrevFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)) } returns
                FeedNavigationResult(listOf(prev1), true)
            every { feedGetService.findNextFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)) } returns
                FeedNavigationResult(listOf(next1), false)

            val allMedias =
                listOf(
                    MediaTestFixture.createMedia(1L, currentFeed),
                    MediaTestFixture.createMedia(2L, prev1),
                    MediaTestFixture.createMedia(3L, next1),
                )
            every { mediaGetService.findAllByFeeds(match { it.size == 3 }) } returns allMedias

            every { linkedUserGetService.findAll(any<Feed>()) } returns listOf(mockk<LinkedUser>())
            every { commentGetService.findAllByFeed(any()) } returns listOf(mockk<Comment>())

            val expected = mockk<FeedNavigationResponse>()
            every {
                feedMapper.toFeedNavigationResponse(
                    eq(currentFeed),
                    eq(listOf(prev1)),
                    eq(listOf(next1)),
                    any(),
                    any(),
                    any(),
                    eq(true),
                    eq(false),
                )
            } returns expected

            When("피드 네비게이션을 조회하면") {
                val result = feedUsecase.getFeedNavigation(feedId, currentUserId, 1, 1)

                Then("이전/다음 피드 정보가 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedGetService.findById(feedId) }
                    verify { userGetService.findById(currentUserId) }
                    verify { userBlockService.findAllByBlocker(currentUser) }
                    verify { feedGetService.findPrevFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)) }
                    verify { feedGetService.findNextFeedsWithHasMore(eq(currentFeed), eq(blocked), eq(1)) }
                    verify { mediaGetService.findAllByFeeds(any()) }
                    verify(atLeast = 1) { linkedUserGetService.findAll(any<Feed>()) }
                    verify(atLeast = 1) { commentGetService.findAllByFeed(any()) }
                }
            }
        }

        Given("네비게이션 크기가 null일 때") {
            val feedId = 10L
            val currentUserId = 1L

            val me = UserTestFixture.createUser(currentUserId, "me")
            every { userGetService.findById(currentUserId) } returns me

            val author = UserTestFixture.createUser(2L, "author")
            val current = FeedTestFixture.createFeed(feedId, author)
            every { feedGetService.findById(feedId) } returns current

            every { userBlockService.findAllByBlocker(me) } returns emptyList()

            every { feedGetService.findPrevFeedsWithHasMore(eq(current), any(), eq(1)) } returns
                FeedNavigationResult(emptyList(), false)
            every { feedGetService.findNextFeedsWithHasMore(eq(current), any(), eq(1)) } returns
                FeedNavigationResult(emptyList(), false)

            every { mediaGetService.findAllByFeeds(any()) } returns emptyList()
            every { linkedUserGetService.findAll(any<Feed>()) } returns emptyList()
            every { commentGetService.findAllByFeed(any()) } returns emptyList()

            val expected = mockk<FeedNavigationResponse>()
            every {
                feedMapper.toFeedNavigationResponse(
                    eq(current),
                    eq(emptyList()),
                    eq(emptyList()),
                    any(),
                    any(),
                    any(),
                    eq(false),
                    eq(false),
                )
            } returns expected

            When("기본 크기로 네비게이션을 조회하면") {
                val result = feedUsecase.getFeedNavigation(feedId, currentUserId, null, null)

                Then("기본 크기로 조회되어야 한다") {
                    result shouldBe expected
                    verify { feedGetService.findPrevFeedsWithHasMore(eq(current), any(), eq(1)) }
                    verify { feedGetService.findNextFeedsWithHasMore(eq(current), any(), eq(1)) }
                }
            }
        }

        Given("피드 업로드 요청이 주어졌을 때") {
            val userId = 1L
            val author = UserTestFixture.createUser(userId, "me")
            every { userGetService.findById(userId) } returns author

            val request =
                FeedUploadRequest(
                    "desc",
                    listOf(FeedMediaRequest(1, "https://example.com/a.png", MediaType.IMAGE)),
                    listOf(2L, 3L),
                )

            val feed = FeedTestFixture.createFeed(100L, author)
            every { feedMapper.toFeed(author, request.description) } returns feed

            every { mediaMapper.toMedia(eq(feed), any()) } returns MediaTestFixture.createMedia(1L, feed)

            val u2 = UserTestFixture.createUser(2L, "u2")
            val u3 = UserTestFixture.createUser(3L, "u3")
            every { userGetService.findAll(request.userIds) } returns listOf(u2, u3)

            every { feedUserMapper.toLinkedUser(any(), eq(feed)) } returns mockk<LinkedUser>()

            When("피드를 업로드하면") {
                feedUsecase.uploadFeed(userId, request)

                Then("피드, 미디어, 링크, 알림이 저장되어야 한다") {
                    verify { feedSaveService.save(feed) }
                    verify { mediaSaveService.saveAll(any()) }
                    verify { linkedUserSaveService.saveAll(any()) }
                    verify(atLeast = 1) { eventPublisher.publishEvent(any<Any>()) }
                }
            }
        }

        Given("자신의 피드에 공감하려고 할 때") {
            val userId = 1L
            val feedId = 10L

            val me = UserTestFixture.createUser(userId, "me")
            val myFeed = FeedTestFixture.createFeed(feedId, me)

            every { feedGetService.findByIdWithLock(feedId) } returns myFeed
            every { userGetService.findById(userId) } returns me

            val request = ReactionRequest(1L)

            When("자신의 피드에 공감하면") {
                Then("예외가 발생해야 한다") {
                    shouldThrow<SelfReactionNotAllowedException> {
                        feedUsecase.reactToFeed(userId, feedId, request)
                    }
                }
            }
        }

        Given("기존 리액션이 있고 기준치를 넘을 때") {
            val userId = 1L
            val feedId = 10L

            val me = UserTestFixture.createUser(userId, "me")
            val author = UserTestFixture.createUser(2L, "author")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findByIdWithLock(feedId) } returns feed
            every { userGetService.findById(userId) } returns me

            val reaction = ReactionTestFixture.createReaction(feed, me, 4)
            every { reactionGetService.findByFeedAndUser(feed, me) } returns Optional.of(reaction)

            val request = ReactionRequest(1L)

            When("리액션을 추가하면") {
                feedUsecase.reactToFeed(userId, feedId, request)

                Then("업데이트와 알림이 저장되어야 한다") {
                    verify { feedUpdateService.updateTotalReaction(feed, reaction, author, 1L) }
                    verify(atLeast = 1) { eventPublisher.publishEvent(any<Any>()) }
                }
            }
        }

        Given("기존 리액션이 없을 때") {
            val userId = 1L
            val feedId = 10L

            val me = UserTestFixture.createUser(userId, "me")
            val author = UserTestFixture.createUser(2L, "author")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findByIdWithLock(feedId) } returns feed
            every { userGetService.findById(userId) } returns me

            every { reactionGetService.findByFeedAndUser(feed, me) } returns Optional.empty()

            val toSave = ReactionTestFixture.createReaction(feed, me, 0)
            val saved = ReactionTestFixture.createReaction(feed, me, 0)

            every { reactionMapper.toReaction(me, feed, 0L) } returns toSave
            every { reactionSaveService.save(toSave) } returns saved

            val request = ReactionRequest(1L)

            When("첫 리액션을 추가하면") {
                feedUsecase.reactToFeed(userId, feedId, request)

                Then("리액션이 저장되고 첫 리액션 알림만 저장되어야 한다") {
                    verify { reactionSaveService.save(toSave) }
                    verify { feedUpdateService.updateTotalReaction(feed, saved, author, 1L) }
                    verify(atLeast = 1) { eventPublisher.publishEvent(any<Any>()) }
                }
            }
        }

        Given("댓글 작성 요청이 주어졌을 때") {
            val userId = 1L
            val feedId = 10L

            val me = UserTestFixture.createUser(userId, "me")
            val author = UserTestFixture.createUser(2L, "author")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { userGetService.findById(userId) } returns me
            every { feedGetService.findById(feedId) } returns feed

            val request = CommentWriteRequest("hello")
            val comment = CommentTestFixture.createComment(1L, me, feed, "hello")
            every { commentMapper.toComment(me, feed, request) } returns comment

            When("댓글을 작성하면") {
                feedUsecase.writeComment(userId, feedId, request)

                Then("댓글이 저장되어야 한다") {
                    verify { commentSaveService.saveComment(comment) }
                }
            }
        }

        Given("피드에 여러 리액션이 있을 때") {
            val feedId = 10L

            val author = UserTestFixture.createUser(2L, "author")
            val u1 = UserTestFixture.createUser(1L, "u1")
            val u2 = UserTestFixture.createUser(3L, "u2")

            val feed = FeedTestFixture.createFeed(feedId, author)

            val r1 = ReactionTestFixture.createReaction(feed, u1, 5)
            val r2 = ReactionTestFixture.createReaction(feed, u2, 1)

            every { feedGetService.findById(feedId) } returns feed
            every { reactionGetService.findAll(feed) } returns listOf(r1, r2)

            val resp1 = mockk<ReactionUserResponse>()
            val resp2 = mockk<ReactionUserResponse>()

            every { reactionMapper.toResponse(r1) } returns resp1
            every { reactionMapper.toResponse(r2) } returns resp2

            When("리액션 목록을 조회하면") {
                val result = feedUsecase.getReactionUser(feedId)

                Then("모든 리액션이 반환되어야 한다") {
                    result shouldContainExactly listOf(resp1, resp2)
                    verify(exactly = 2) { reactionMapper.toResponse(any()) }
                }
            }
        }

        Given("다른 사용자가 피드를 수정하려고 할 때") {
            val userId = 1L
            val feedId = 10L

            val author = UserTestFixture.createUser(2L, "author")
            val other = UserTestFixture.createUser(userId, "other")

            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findById(feedId) } returns feed
            every { userGetService.findById(userId) } returns other

            val request = FeedUpdateRequest("hello", null, null)

            When("피드를 수정하면") {
                Then("예외가 발생해야 한다") {
                    shouldThrow<FeedUpdateNotAllowedException> {
                        feedUsecase.updateFeed(userId, feedId, request)
                    }
                }
            }
        }

        Given("피드 수정 시 미디어와 링크 유저가 있을 때") {
            val userId = 1L
            val feedId = 10L

            val author = UserTestFixture.createUser(userId, "author")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findById(feedId) } returns feed
            every { userGetService.findById(userId) } returns author

            val mr = FeedMediaRequest(1, "https://example.com/a.jpg", MediaType.IMAGE)
            val request = FeedUpdateRequest("new", listOf(mr), listOf(2L))

            every { mediaMapper.toMedia(eq(feed), eq(mr)) } returns MediaTestFixture.createMedia(1L, feed)

            val u2 = UserTestFixture.createUser(2L, "u2")
            every { userGetService.findAll(request.userIds!!) } returns listOf(u2)
            every { feedUserMapper.toLinkedUser(any(), eq(feed)) } returns mockk<LinkedUser>()

            When("피드를 수정하면") {
                feedUsecase.updateFeed(userId, feedId, request)

                Then("기존 미디어와 링크가 삭제되고 새로 저장되어야 한다") {
                    verify { feedUpdateService.update(feed, request) }
                    verify { mediaDeleteService.deleteAllByFeed(feed) }
                    verify { mediaSaveService.saveAll(any()) }
                    verify { linkedUserDeleteService.deleteAllByFeed(feed) }
                    verify { linkedUserSaveService.saveAll(any()) }
                }
            }
        }

        Given("내 피드 목록을 조회할 때") {
            val userId = 1L
            val pageNumber = 0
            val pageSize = 10

            val user = UserTestFixture.createUser(userId, "me")
            every { userGetService.findById(userId) } returns user

            val f1 = FeedTestFixture.createFeed(100L, user)

            val slice = SliceImpl(listOf(f1), PageRequest.of(pageNumber, pageSize), false)
            every { feedGetService.findAllByUser(eq(user), any()) } returns slice

            every { mediaGetService.findAllByFeeds(slice.content) } returns
                listOf(MediaTestFixture.createMedia(1L, f1))

            val expected = mockk<FeedListResponse>()
            every { feedMapper.toFeedListResponse(eq(user), eq(slice), any(), eq(true)) } returns expected

            When("내 피드를 조회하면") {
                val result = feedUsecase.getMyFeeds(userId, pageNumber, pageSize)

                Then("내 피드 목록이 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedMapper.toFeedListResponse(eq(user), eq(slice), any(), eq(true)) }
                }
            }
        }

        Given("다른 사용자의 피드 목록을 조회할 때") {
            val otherUserId = 2L
            val pageNumber = 0
            val pageSize = 10

            val other = UserTestFixture.createUser(otherUserId, "other")
            every { userGetService.findById(otherUserId) } returns other

            val f1 = FeedTestFixture.createFeed(100L, other)

            val slice = SliceImpl(listOf(f1), PageRequest.of(pageNumber, pageSize), false)
            every { feedGetService.findAllByUser(eq(other), any()) } returns slice

            every { mediaGetService.findAllByFeeds(slice.content) } returns
                listOf(MediaTestFixture.createMedia(1L, f1))

            val expected = mockk<FeedListResponse>()
            every { feedMapper.toFeedListResponse(eq(other), eq(slice), any(), eq(false)) } returns expected

            When("다른 사람의 피드를 조회하면") {
                val result = feedUsecase.getOthersFeeds(otherUserId, pageNumber, pageSize)

                Then("다른 사람의 피드 목록이 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedMapper.toFeedListResponse(eq(other), eq(slice), any(), eq(false)) }
                }
            }
        }

        Given("함께한 피드 목록을 조회할 때") {
            val userId = 1L
            val pageNumber = 0
            val pageSize = 10

            val user = UserTestFixture.createUser(userId, "me")
            every { userGetService.findById(userId) } returns user

            val author = UserTestFixture.createUser(2L, "author")
            val f1 = FeedTestFixture.createFeed(100L, author)

            val slice = SliceImpl(listOf(f1), PageRequest.of(pageNumber, pageSize), false)
            every { linkedUserGetService.findAllByUser(eq(user), any()) } returns slice

            every { mediaGetService.findAllByFeeds(slice.content) } returns
                listOf(MediaTestFixture.createMedia(1L, f1))

            val expected = mockk<FeedListResponse>()
            every { feedMapper.toFeedListResponse(eq(slice), any()) } returns expected

            When("함께한 피드를 조회하면") {
                val result = feedUsecase.getLinkedFeeds(userId, pageNumber, pageSize)

                Then("함께한 피드 목록이 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedMapper.toFeedListResponse(eq(slice), any()) }
                }
            }
        }

        Given("전체 사용자를 조회할 때") {
            val u1 = UserTestFixture.createUser(1L, "me")
            val u2 = UserTestFixture.createUser(2L, "me2")
            every { userGetService.findAll() } returns listOf(u1, u2)

            val r1 = mockk<FeedUserResponse>()
            val r2 = mockk<FeedUserResponse>()
            every { feedUserMapper.toFeedUserResponse(eq(u1)) } returns r1
            every { feedUserMapper.toFeedUserResponse(eq(u2)) } returns r2

            When("모든 사용자를 조회하면") {
                val result = feedUsecase.getAllUser()

                Then("모든 사용자가 반환되어야 한다") {
                    result shouldContainExactly listOf(r1, r2)
                    verify(exactly = 2) { feedUserMapper.toFeedUserResponse(any()) }
                }
            }
        }

        Given("페이지로 사용자를 조회할 때") {
            val pageNumber = 0
            val pageSize = 10

            val u1 = UserTestFixture.createUser(1L, "me")
            val slice = SliceImpl(listOf(u1), PageRequest.of(pageNumber, pageSize), true)
            every { userGetService.findAll(any<PageRequest>()) } returns slice

            val expected = mockk<FeedUserListResponse>()
            every { feedUserMapper.toFeedUserListResponse(slice) } returns expected

            When("사용자 목록을 조회하면") {
                val result = feedUsecase.getUsers(pageNumber, pageSize)

                Then("사용자 목록이 반환되어야 한다") {
                    result shouldBe expected
                    verify { feedUserMapper.toFeedUserListResponse(slice) }
                }
            }
        }

        Given("피드 작성자가 피드를 삭제하려고 할 때") {
            val userId = 1L
            val feedId = 10L

            val author = UserTestFixture.createUser(userId, "me")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findById(feedId) } returns feed
            every { userGetService.findById(userId) } returns author

            When("피드를 삭제하면") {
                feedUsecase.deleteFeed(userId, feedId)

                Then("피드가 삭제되어야 한다") {
                    verify { feedDeleteService.delete(feed) }
                }
            }
        }

        Given("다른 사용자가 피드를 삭제하려고 할 때") {
            val userId = 1L
            val feedId = 10L

            val author = UserTestFixture.createUser(2L, "me")
            val other = UserTestFixture.createUser(userId, "me2")

            val feed = FeedTestFixture.createFeed(feedId, author)

            every { feedGetService.findById(feedId) } returns feed
            every { userGetService.findById(userId) } returns other

            When("피드를 삭제하면") {
                Then("예외가 발생해야 한다") {
                    shouldThrow<FeedDeleteNotAllowedException> {
                        feedUsecase.deleteFeed(userId, feedId)
                    }
                }
            }
        }

        Given("댓글 작성자가 댓글을 삭제하려고 할 때") {
            val userId = 1L
            val commentId = 5L

            val author = UserTestFixture.createUser(2L, "me")
            val me = UserTestFixture.createUser(userId, "me2")
            val feed = FeedTestFixture.createFeed(10L, author)

            val comment = CommentTestFixture.createComment(commentId, me, feed, "wq")
            every { userGetService.findById(userId) } returns me
            every { commentGetService.findCommentByIdNotDeleted(commentId) } returns comment

            When("댓글을 삭제하면") {
                feedUsecase.deleteComment(userId, commentId)

                Then("댓글이 삭제되어야 한다") {
                    verify { commentDeleteService.deleteComment(comment) }
                }
            }
        }

        Given("다른 사용자가 댓글을 삭제하려고 할 때") {
            val userId = 1L
            val commentId = 5L

            val author = UserTestFixture.createUser(2L, "me")
            val me = UserTestFixture.createUser(userId, "me2")
            val other = UserTestFixture.createUser(3L, "me3")
            val feed = FeedTestFixture.createFeed(10L, author)

            val comment = CommentTestFixture.createComment(commentId, other, feed, "wq")
            every { userGetService.findById(userId) } returns me
            every { commentGetService.findCommentByIdNotDeleted(commentId) } returns comment

            When("댓글을 삭제하면") {
                Then("예외가 발생해야 한다") {
                    shouldThrow<CommentDeleteNotAllowedException> {
                        feedUsecase.deleteComment(userId, commentId)
                    }
                }
            }
        }

        Given("피드 신고 요청이 주어졌을 때") {
            val userId = 1L
            val feedId = 10L

            val me = UserTestFixture.createUser(userId, "me")
            val author = UserTestFixture.createUser(2L, "me")
            val feed = FeedTestFixture.createFeed(feedId, author)

            every { userGetService.findById(userId) } returns me
            every { feedGetService.findById(feedId) } returns feed

            val request = FeedReportRequest("신고")

            When("피드를 신고하면") {
                feedUsecase.reportFeed(userId, feedId, request)

                Then("노션과 슬랙으로 신고 내용이 전송되어야 한다") {
                    verify { notionDatabaseService.sendFeedReport(request.report, me.id!!, feed.id!!) }
                    verify { slackWebhookService.sendFeedReport(request.report) }
                }
            }
        }
    })
