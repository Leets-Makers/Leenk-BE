package leets.leenk.global.common.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.servlet.resource.NoResourceFoundException

class GlobalExceptionHandlerTest :
    DescribeSpec({
        val handler = GlobalExceptionHandler()

        describe("GlobalExceptionHandler") {
            context("BaseException 처리") {
                it("BaseException을 처리하여 에러 응답을 반환해야 한다") {
                    val exception = HandlerTestException(CommonErrorCode.INTERNAL_SERVER_ERROR)

                    val response = handler.handleException(exception)

                    response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 3001
                    response.body!!.message shouldBe "서버 내부 오류입니다."
                    response.body!!.data shouldBe null
                }
            }

            context("BaseException with custom message 처리") {
                it("커스텀 메시지를 포함한 에러 응답을 반환해야 한다") {
                    val customMessage = "커스텀 에러 메시지"
                    val exception =
                        HandlerTestExceptionWithMessage(CommonErrorCode.INTERNAL_SERVER_ERROR, customMessage)

                    val response = handler.handleException(exception)

                    response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 3001
                    response.body!!.message shouldBe customMessage
                    response.body!!.data shouldBe null
                }
            }

            context("ResourceLockedException 처리") {
                it("ResourceLockedException을 처리하여 CONFLICT 응답을 반환해야 한다") {
                    val exception = ResourceLockedException()

                    val response = handler.handleException(exception)

                    response.statusCode shouldBe HttpStatus.CONFLICT
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 3003
                    response.body!!.message shouldBe "다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요."
                    response.body!!.data shouldBe null
                }
            }

            context("MethodArgumentNotValidException 처리") {
                it("validation 에러 목록을 반환해야 한다") {
                    val fieldError = FieldError("testObject", "name", "", false, null, null, "이름은 필수입니다")
                    val bindingResult = org.springframework.validation.BeanPropertyBindingResult(Any(), "testObject")
                    bindingResult.addError(fieldError)

                    val exception =
                        MethodArgumentNotValidException(
                            org.springframework.core.MethodParameter.forExecutable(
                                GlobalExceptionHandlerTest::class.java.getDeclaredConstructor(),
                                -1,
                            ),
                            bindingResult,
                        )

                    val response = handler.handleValidation(exception)

                    response.statusCode shouldBe HttpStatus.BAD_REQUEST
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4001
                    response.body!!.message shouldBe "잘못된 인자입니다."
                    response.body!!.data shouldNotBe null
                    response.body!!.data!!.size shouldBe 1
                    response.body!!.data!![0].errorField shouldBe "name"
                    response.body!!.data!![0].errorMessage shouldBe "이름은 필수입니다"
                }
            }

            context("IllegalArgumentException 처리") {
                it("BAD_REQUEST 응답을 반환해야 한다") {
                    val exception = IllegalArgumentException("잘못된 인자입니다")

                    val response = handler.handleIllegalArgument(exception)

                    response.statusCode shouldBe HttpStatus.BAD_REQUEST
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4001
                    response.body!!.message shouldBe "잘못된 인자입니다."
                    response.body!!.data shouldBe null
                }
            }

            context("NoResourceFoundException 처리") {
                it("NOT_FOUND 응답을 반환해야 한다") {
                    val exception = NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/api/test")
                    val response = handler.handleNoResourceFound(exception)

                    response.statusCode shouldBe HttpStatus.NOT_FOUND
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4003
                    response.body!!.message shouldBe "요청하신 리소스를 찾을 수 없습니다."
                    response.body!!.data shouldBe null
                }
            }

            context("HttpRequestMethodNotSupportedException 처리") {
                it("METHOD_NOT_ALLOWED 응답을 반환해야 한다") {
                    val exception = HttpRequestMethodNotSupportedException("POST")

                    val response = handler.handleMethodNotAllowed(exception)

                    response.statusCode.value() shouldBe 405
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4004
                    response.body!!.message shouldBe "지원하지 않는 HTTP 메서드입니다."
                }
            }

            context("HttpMessageNotReadableException 처리") {
                it("JSON_PARSE_ERROR 응답을 반환해야 한다") {
                    val exception =
                        HttpMessageNotReadableException(
                            "JSON parse error",
                            MockHttpInputMessage(ByteArray(0)),
                        )

                    val response = handler.handleMessageNotReadable(exception)

                    response.statusCode shouldBe HttpStatus.BAD_REQUEST
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4002
                    response.body!!.message shouldNotBe null
                }
            }

            context("HttpMessageNotReadableException with BaseException cause 처리") {
                it("cause의 ErrorCode를 사용해야 한다") {
                    val cause = HandlerTestException(CommonErrorCode.INVALID_ARGUMENT)
                    val exception =
                        HttpMessageNotReadableException(
                            "JSON parse error",
                            cause,
                            MockHttpInputMessage(ByteArray(0)),
                        )

                    val response = handler.handleMessageNotReadable(exception)

                    response.statusCode shouldBe HttpStatus.BAD_REQUEST
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 4001
                }
            }

            context("일반 Exception 처리") {
                it("INTERNAL_SERVER_ERROR 응답을 반환해야 한다") {
                    val exception = RuntimeException("예상치 못한 에러")

                    val response = handler.handleAll(exception)

                    response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
                    response.body shouldNotBe null
                    response.body!!.code shouldBe 3001
                    response.body!!.message shouldBe "예상치 못한 에러"
                }
            }
        }
    })

// 테스트용 예외 클래스
internal class HandlerTestException(
    errorCode: ErrorCodeInterface,
) : BaseException(errorCode)

internal class HandlerTestExceptionWithMessage(
    errorCode: ErrorCodeInterface,
    message: String,
) : BaseException(errorCode, message)
