package leets.leenk.domain.media.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.media.application.dto.response.MediaUrlResponse
import leets.leenk.domain.media.application.exception.MediaNotFoundException
import leets.leenk.domain.media.domain.entity.enums.DomainType
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaUpdateService
import leets.leenk.domain.media.domain.service.S3PresignedUrlService
import leets.leenk.domain.media.test.fixture.MediaTestFixture

class MediaUsecaseTest :
    DescribeSpec({
        val s3PresignedUrlService = mockk<S3PresignedUrlService>()
        val mediaGetService = mockk<MediaGetService>()
        val mediaUpdateService = mockk<MediaUpdateService>()
        val mediaUsecase = MediaUsecase(s3PresignedUrlService, mediaGetService, mediaUpdateService)

        afterEach {
            clearMocks(s3PresignedUrlService, mediaGetService, mediaUpdateService)
        }

        describe("getUrl") {
            context("파일 이름 목록과 도메인 타입이 주어졌을 때") {
                it("S3 Presigned URL 목록을 반환해야 한다") {
                    val domainType = DomainType.FEED
                    val fileNames = listOf("image1.jpg", "image2.jpg")
                    val expected =
                        listOf(
                            MediaUrlResponse("image1.jpg", "https://s3.example.com/image1.jpg"),
                            MediaUrlResponse("image2.jpg", "https://s3.example.com/image2.jpg"),
                        )

                    every { s3PresignedUrlService.generateUrlList(domainType, fileNames) } returns expected

                    val result = mediaUsecase.getUrl(domainType, fileNames)

                    result shouldBe expected
                    verify(exactly = 1) { s3PresignedUrlService.generateUrlList(domainType, fileNames) }
                }
            }
        }

        describe("updateThumbnailUrl") {
            context("존재하는 미디어 URL로 썸네일 업데이트 시") {
                it("썸네일 URL을 업데이트해야 한다") {
                    val originalUrl = "https://example.com/original.jpg"
                    val thumbnailUrl = "https://example.com/thumbnail.jpg"
                    val media = MediaTestFixture.createMedia(mediaUrl = originalUrl)

                    every { mediaGetService.findByMediaUrl(originalUrl) } returns media
                    justRun { mediaUpdateService.update(media, thumbnailUrl) }

                    mediaUsecase.updateThumbnailUrl(originalUrl, thumbnailUrl)

                    verify(exactly = 1) { mediaGetService.findByMediaUrl(originalUrl) }
                    verify(exactly = 1) { mediaUpdateService.update(media, thumbnailUrl) }
                }
            }

            context("존재하지 않는 미디어 URL로 썸네일 업데이트 시") {
                it("MediaNotFoundException이 발생해야 한다") {
                    val notExistUrl = "https://example.com/not-exist.jpg"
                    val thumbnailUrl = "https://example.com/thumbnail.jpg"

                    every { mediaGetService.findByMediaUrl(notExistUrl) } throws MediaNotFoundException()

                    shouldThrow<MediaNotFoundException> {
                        mediaUsecase.updateThumbnailUrl(notExistUrl, thumbnailUrl)
                    }

                    verify(exactly = 0) { mediaUpdateService.update(any(), any()) }
                }
            }
        }
    })
