package leets.leenk.domain.media.domain.service

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.media.domain.repository.MediaRepository
import leets.leenk.domain.media.test.fixture.MediaTestFixture

class MediaDeleteServiceTest :
    DescribeSpec({
        val mediaRepository = mockk<MediaRepository>()
        val mediaDeleteService = MediaDeleteService(mediaRepository)

        describe("deleteAllByFeed") {
            context("Feed를 전달하면") {
                val user = UserTestFixture.createUser(1L, "user1")
                val feed = FeedTestFixture.createFeed(1L, user)

                justRun { mediaRepository.deleteAllByFeed(feed) }
                justRun { mediaRepository.flush() }

                it("deleteAllByFeed와 flush가 순서대로 호출되어야 한다") {
                    mediaDeleteService.deleteAllByFeed(feed)

                    verifySequence {
                        mediaRepository.deleteAllByFeed(feed)
                        mediaRepository.flush()
                    }
                }
            }
        }

        describe("delete") {
            context("Media를 전달하면") {
                val media = MediaTestFixture.createMedia()

                justRun { mediaRepository.delete(media) }

                it("repository의 delete가 호출되어야 한다") {
                    mediaDeleteService.delete(media)

                    verify(exactly = 1) { mediaRepository.delete(media) }
                }
            }
        }

        describe("deleteAll") {
            context("Media 리스트를 전달하면") {
                val mediaList =
                    listOf(
                        MediaTestFixture.createMedia(id = 1L),
                        MediaTestFixture.createMedia(id = 2L),
                    )

                justRun { mediaRepository.deleteAll(mediaList) }

                it("repository의 deleteAll이 호출되어야 한다") {
                    mediaDeleteService.deleteAll(mediaList)

                    verify(exactly = 1) { mediaRepository.deleteAll(mediaList) }
                }
            }
        }
    })
