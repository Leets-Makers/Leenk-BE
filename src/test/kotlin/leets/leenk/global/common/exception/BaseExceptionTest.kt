package leets.leenk.global.common.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

// TODO: 코틀린 스타일로 변경 후 이 주석을 제거합니다.
class BaseExceptionTest :
    DescribeSpec({
        describe("BaseException") {
            context("ErrorCodeInterface만으로 생성 시") {
                it("ErrorCode의 메시지를 사용해야 한다") {
                    val exception = SimpleTestException(ErrorCode.INTERNAL_SERVER_ERROR)

                    exception.message shouldBe ErrorCode.INTERNAL_SERVER_ERROR.message
                    exception.errorCode shouldBe ErrorCode.INTERNAL_SERVER_ERROR
                }
            }

            context("ErrorCodeInterface와 커스텀 메시지로 생성 시") {
                it("커스텀 메시지를 사용해야 한다") {
                    val customMessage = "커스텀 에러 메시지"
                    val exception = SimpleTestExceptionWithMessage(ErrorCode.INTERNAL_SERVER_ERROR, customMessage)

                    exception.message shouldBe customMessage
                    exception.errorCode shouldBe ErrorCode.INTERNAL_SERVER_ERROR
                }
            }

            context("ErrorCodeInterface의 속성 접근") {
                it("ErrorCode의 모든 속성에 접근할 수 있어야 한다") {
                    val exception = SimpleTestException(ErrorCode.INVALID_ARGUMENT)

                    exception.errorCode.code shouldBe 4001
                    exception.errorCode.status shouldBe HttpStatus.BAD_REQUEST
                    exception.errorCode.message shouldBe "잘못된 인자입니다."
                }
            }
        }
    })

// 테스트용 구체 클래스
private class SimpleTestException(
    errorCode: ErrorCodeInterface,
) : BaseException(errorCode)

private class SimpleTestExceptionWithMessage(
    errorCode: ErrorCodeInterface,
    message: String,
) : BaseException(errorCode, message)
