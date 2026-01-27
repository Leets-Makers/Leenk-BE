package leets.leenk.global.common.exception.response

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

// TODO: 코틀린 스타일로 변경 후 이 주석을 제거합니다.
class ValidErrorResponseTest :
    DescribeSpec({
        describe("ValidErrorResponse") {
            context("팩토리 메서드로 생성 시") {
                it("필드 정보를 포함한 에러 응답을 생성해야 한다") {
                    val field = "email"
                    val message = "이메일 형식이 올바르지 않습니다"
                    val value = "invalid-email"

                    val response = ValidErrorResponse.of(field, message, value)

                    response.errorField shouldBe field
                    response.errorMessage shouldBe message
                    response.inputValue shouldBe value
                }
            }

            context("직접 생성 시") {
                it("모든 필드가 올바르게 설정되어야 한다") {
                    val response =
                        ValidErrorResponse(
                            "password",
                            "비밀번호는 8자 이상이어야 합니다",
                            "123",
                        )

                    response.errorField shouldBe "password"
                    response.errorMessage shouldBe "비밀번호는 8자 이상이어야 합니다"
                    response.inputValue shouldBe "123"
                }
            }

            context("null 값 처리") {
                it("inputValue가 null이어도 정상적으로 처리해야 한다") {
                    val response = ValidErrorResponse.of("username", "필수 입력 항목입니다", null)

                    response.errorField shouldBe "username"
                    response.errorMessage shouldBe "필수 입력 항목입니다"
                    response.inputValue shouldBe null
                }
            }

            context("다양한 타입의 inputValue") {
                it("정수 값을 inputValue로 가질 수 있어야 한다") {
                    val response = ValidErrorResponse.of("age", "나이는 18세 이상이어야 합니다", 15)

                    response.inputValue shouldBe 15
                }

                it("리스트를 inputValue로 가질 수 있어야 한다") {
                    val list = listOf("a", "b")
                    val response = ValidErrorResponse.of("tags", "태그는 최대 5개까지 가능합니다", list)

                    response.inputValue shouldBe list
                }
            }
        }
    })
