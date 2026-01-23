package leets.leenk.global.common.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.Slice

class PageableMapperUtilTest :
    DescribeSpec({

        describe("매퍼와 CommonPageableResponse 객체 간의 예외 케이스를 방지하기 위한 테스트") {
            context("Slice 객체가 주어지면") {
                val slice =
                    mockk<Slice<Any>> {
                        every { number } returns 2
                        every { size } returns 10
                        every { numberOfElements } returns 8
                        every { hasNext() } returns true
                        every { isEmpty } returns false
                    }
                it("필드가 올바르게 매핑된다") {
                    val result = PageableMapperUtil.from(slice)

                    result.pageNumber shouldBe 2
                    result.pageSize shouldBe 10
                    result.numberOfElements shouldBe 8
                    result.hasNext shouldBe true
                    result.empty shouldBe false
                }
            }

            context("Page 객체가 주어지면") {
                val page =
                    mockk<Page<Any>> {
                        every { number } returns 1
                        every { size } returns 20
                        every { numberOfElements } returns 15
                        every { hasNext() } returns false
                        every { isEmpty } returns false
                    }

                it("필드가 올바르게 매핑된다") {
                    val result = PageableMapperUtil.from(page)

                    result.pageNumber shouldBe 1
                    result.pageSize shouldBe 20
                    result.numberOfElements shouldBe 15
                    result.hasNext shouldBe false
                    result.empty shouldBe false
                }
            }
        }
    })
