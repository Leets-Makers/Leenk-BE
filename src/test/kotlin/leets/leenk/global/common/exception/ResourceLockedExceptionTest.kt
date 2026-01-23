package leets.leenk.global.common.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class ResourceLockedExceptionTest :
    DescribeSpec({
        describe("ResourceLockedException") {
            context("예외 생성 시") {
                it("RESOURCE_LOCKED 에러 코드를 사용해야 한다") {
                    val exception = ResourceLockedException()

                    exception.errorCode shouldBe ErrorCode.RESOURCE_LOCKED
                    exception.errorCode.code shouldBe 3003
                    exception.errorCode.status shouldBe HttpStatus.CONFLICT
                    exception.message shouldBe "다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요."
                }
            }

            context("BaseException 상속") {
                it("BaseException의 인스턴스이어야 한다") {
                    val exception = ResourceLockedException()

                    assert(exception is BaseException)
                }
            }
        }
    })
