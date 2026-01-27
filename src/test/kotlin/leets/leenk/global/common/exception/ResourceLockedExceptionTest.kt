package leets.leenk.global.common.exception

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class ResourceLockedExceptionTest :
    StringSpec({
        "ResourceLockedException은 RESOURCE_LOCKED 에러 코드를 사용해야 한다" {
            val exception = ResourceLockedException()

            exception.errorCode shouldBe ErrorCode.RESOURCE_LOCKED
            exception.errorCode.code shouldBe 3003
            exception.errorCode.status shouldBe HttpStatus.CONFLICT
        }

        "ResourceLockedException은 BaseException의 인스턴스이어야 한다" {
            val exception = ResourceLockedException()

            assert(exception is BaseException)
        }
    })
