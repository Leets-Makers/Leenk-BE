package leets.leenk.domain.media.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.media.application.exception.MediaNotFoundException
import leets.leenk.domain.media.domain.repository.MediaRepository
import leets.leenk.domain.media.test.fixture.MediaTestFixture
import java.util.Optional

class MediaGetServiceTest :
    DescribeSpec({
        val mediaRepository = mockk<MediaRepository>()
        val mediaGetService = MediaGetService(mediaRepository)

        val user = UserTestFixture.createUser(1L, "user1")
        val location = LocationTestFixture.createLocation()
        val leenk = LeenkTestFixture.createLeenk(id = 1L, author = user, location = location)

        describe("findById") {
            context("존재하는 미디어 ID로 조회 시") {
                val media = MediaTestFixture.createMedia(id = 1L)
                every { mediaRepository.findById(1L) } returns Optional.of(media)

                it("미디어를 반환해야 한다") {
                    val result = mediaGetService.findById(1L)
                    result shouldBe media
                }
            }

            context("존재하지 않는 미디어 ID로 조회 시") {
                every { mediaRepository.findById(999L) } returns Optional.empty()

                it("MediaNotFoundException이 발생해야 한다") {
                    shouldThrow<MediaNotFoundException> {
                        mediaGetService.findById(999L)
                    }
                }
            }
        }

        describe("findByMediaUrl") {
            context("존재하는 미디어 URL로 조회 시") {
                val url = "https://example.com/media.jpg"
                val media = MediaTestFixture.createMedia(id = 1L, mediaUrl = url)
                every { mediaRepository.findByMediaUrl(url) } returns Optional.of(media)

                it("미디어를 반환해야 한다") {
                    val result = mediaGetService.findByMediaUrl(url)
                    result shouldBe media
                }
            }

            context("존재하지 않는 미디어 URL로 조회 시") {
                val url = "https://example.com/not-exist.jpg"
                every { mediaRepository.findByMediaUrl(url) } returns Optional.empty()

                it("MediaNotFoundException이 발생해야 한다") {
                    shouldThrow<MediaNotFoundException> {
                        mediaGetService.findByMediaUrl(url)
                    }
                }
            }
        }

        describe("findMediaUrlByLeenk") {
            context("Leenk에 미디어가 존재할 때") {
                val url = "https://example.com/media.jpg"
                val media = MediaTestFixture.createMedia(id = 1L, mediaUrl = url)
                every { mediaRepository.findFirstByLeenkOrderByPositionAsc(leenk) } returns Optional.of(media)

                it("첫 번째 미디어의 URL을 반환해야 한다") {
                    val result = mediaGetService.findMediaUrlByLeenk(leenk)
                    result shouldBe url
                }
            }

            context("Leenk에 미디어가 없을 때") {
                every { mediaRepository.findFirstByLeenkOrderByPositionAsc(leenk) } returns Optional.empty()

                it("빈 문자열을 반환해야 한다") {
                    val result = mediaGetService.findMediaUrlByLeenk(leenk)
                    result shouldBe ""
                }
            }
        }
    })
