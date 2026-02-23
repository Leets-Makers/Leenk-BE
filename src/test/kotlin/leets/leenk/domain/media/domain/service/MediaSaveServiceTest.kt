package leets.leenk.domain.media.domain.service

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.media.domain.repository.MediaRepository
import leets.leenk.domain.media.test.fixture.MediaTestFixture

class MediaSaveServiceTest :
    DescribeSpec({
        val mediaRepository = mockk<MediaRepository>()
        val mediaSaveService = MediaSaveService(mediaRepository)

        describe("save") {
            context("미디어 엔티티를 저장할 때") {
                val media = MediaTestFixture.createMedia(id = 1L)
                every { mediaRepository.save(media) } returns media

                it("repository.save가 호출되어야 한다") {
                    mediaSaveService.save(media)
                    verify(exactly = 1) { mediaRepository.save(media) }
                }
            }
        }

        describe("saveAll") {
            context("미디어 엔티티 목록을 저장할 때") {
                val mediaList =
                    listOf(
                        MediaTestFixture.createMedia(id = 1L),
                        MediaTestFixture.createMedia(id = 2L),
                    )
                every { mediaRepository.saveAll(mediaList) } returns mediaList

                it("repository.saveAll이 호출되어야 한다") {
                    mediaSaveService.saveAll(mediaList)
                    verify(exactly = 1) { mediaRepository.saveAll(mediaList) }
                }
            }
        }
    })
