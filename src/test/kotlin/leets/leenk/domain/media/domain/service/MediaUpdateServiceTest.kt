package leets.leenk.domain.media.domain.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import leets.leenk.domain.media.test.fixture.MediaTestFixture

class MediaUpdateServiceTest :
    DescribeSpec({
        val mediaUpdateService = MediaUpdateService()

        describe("update") {
            context("미디어와 새로운 썸네일 URL이 주어졌을 때") {
                val newThumbnailUrl = "https://example.com/new-thumbnail.jpg"
                val media = MediaTestFixture.createMedia(thumbnailUrl = "https://example.com/old-thumbnail.jpg")

                it("미디어의 thumbnailUrl이 새로운 URL로 변경되어야 한다") {
                    mediaUpdateService.update(media, newThumbnailUrl)
                    media.thumbnailUrl shouldBe newThumbnailUrl
                }
            }
        }
    })
