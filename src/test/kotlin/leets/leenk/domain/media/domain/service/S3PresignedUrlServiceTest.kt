package leets.leenk.domain.media.domain.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.every
import io.mockk.mockk
import leets.leenk.domain.media.domain.entity.enums.DomainType
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL

class S3PresignedUrlServiceTest :
    DescribeSpec({
        val s3Presigner = mockk<S3Presigner>()
        val service = S3PresignedUrlService(s3Presigner)

        // 필드 주입 대신 리플렉션으로 bucket 값 설정
        val bucketField = S3PresignedUrlService::class.java.getDeclaredField("bucket")
        bucketField.isAccessible = true
        bucketField.set(service, "test-bucket")

        val presignedRequest = mockk<PresignedPutObjectRequest>()
        every { presignedRequest.url() } returns
            URL("https://s3.amazonaws.com/test-bucket/originals/feed/thumbnail_abc.jpg?X-Amz-Signature=test")
        every { s3Presigner.presignPutObject(any<PutObjectPresignRequest>()) } returns presignedRequest

        describe("generateUrlList") {
            context("단일 파일 요청 시") {
                val fileNames = listOf("photo.jpg")

                it("결과 목록의 크기가 1이어야 한다") {
                    val result = service.generateUrlList(DomainType.FEED, fileNames)
                    result shouldHaveSize 1
                }

                it("반환된 파일명이 원본 파일명과 일치해야 한다") {
                    val result = service.generateUrlList(DomainType.FEED, fileNames)
                    result[0].fileName shouldBe "photo.jpg"
                }

                it("반환된 URL이 비어있지 않아야 한다") {
                    val result = service.generateUrlList(DomainType.FEED, fileNames)
                    result[0].mediaUrl shouldContain "https://"
                }
            }

            context("여러 파일 요청 시") {
                val fileNames = listOf("first.jpg", "second.jpg", "third.png")

                it("결과 목록의 크기가 파일 수와 일치해야 한다") {
                    val result = service.generateUrlList(DomainType.FEED, fileNames)
                    result shouldHaveSize 3
                }

                it("각 파일명이 원본 파일명과 순서대로 일치해야 한다") {
                    val result = service.generateUrlList(DomainType.FEED, fileNames)
                    result[0].fileName shouldBe "first.jpg"
                    result[1].fileName shouldBe "second.jpg"
                    result[2].fileName shouldBe "third.png"
                }
            }
        }

        describe("generateKey - thumbnail prefix 로직") {
            // S3 key를 직접 검증하기 위해 presignPutObject 호출 인자를 캡처
            context("첫 번째 파일(index=0) 업로드 시") {
                val capturedRequests = mutableListOf<PutObjectPresignRequest>()
                every { s3Presigner.presignPutObject(capture(capturedRequests)) } returns presignedRequest

                service.generateUrlList(DomainType.FEED, listOf("cover.jpg", "extra.jpg"))
                val firstKey = capturedRequests[0].putObjectRequest().key()

                it("S3 key에 thumbnail_ prefix가 포함되어야 한다") {
                    firstKey shouldContain "thumbnail_"
                }

                it("S3 key에 도메인 타입 경로가 포함되어야 한다") {
                    firstKey shouldContain "originals/feed/"
                }

                it("S3 key에 확장자가 포함되어야 한다") {
                    firstKey shouldContain ".jpg"
                }
            }

            context("두 번째 이후 파일(index>0) 업로드 시") {
                val capturedRequests = mutableListOf<PutObjectPresignRequest>()
                every { s3Presigner.presignPutObject(capture(capturedRequests)) } returns presignedRequest

                service.generateUrlList(DomainType.FEED, listOf("cover.jpg", "extra.jpg"))
                val secondKey = capturedRequests[1].putObjectRequest().key()

                it("S3 key에 thumbnail_ prefix가 없어야 한다") {
                    secondKey shouldNotContain "thumbnail_"
                }

                it("S3 key에 도메인 타입 경로가 포함되어야 한다") {
                    secondKey shouldContain "originals/feed/"
                }
            }

            context("LEENK 도메인 타입으로 업로드 시") {
                val capturedRequests = mutableListOf<PutObjectPresignRequest>()
                every { s3Presigner.presignPutObject(capture(capturedRequests)) } returns presignedRequest

                service.generateUrlList(DomainType.LEENK, listOf("image.png"))
                val key = capturedRequests[0].putObjectRequest().key()

                it("S3 key에 leenk 경로가 포함되어야 한다") {
                    key shouldContain "originals/leenk/"
                }
            }

            context("확장자가 있는 파일명으로 업로드 시") {
                val capturedRequests = mutableListOf<PutObjectPresignRequest>()
                every { s3Presigner.presignPutObject(capture(capturedRequests)) } returns presignedRequest

                service.generateUrlList(DomainType.FEED, listOf("video.mp4"))
                val key = capturedRequests[0].putObjectRequest().key()

                it("원본 확장자가 S3 key에 유지되어야 한다") {
                    key shouldContain ".mp4"
                }
            }
        }
    })
