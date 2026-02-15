package leets.leenk.domain.birthday.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest
import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse
import leets.leenk.domain.birthday.application.exception.BirthdayErrorCode
import leets.leenk.domain.birthday.application.usecase.BirthdayLetterUseCase
import leets.leenk.domain.birthday.application.usecase.BirthdayUsecase
import leets.leenk.domain.user.application.exception.UserErrorCode
import leets.leenk.global.auth.application.annotation.CurrentUserId
import leets.leenk.global.common.exception.ApiErrorCodeExample
import leets.leenk.global.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "BIRTHDAY")
@ApiErrorCodeExample(BirthdayErrorCode::class, UserErrorCode::class)
@RestController
@RequestMapping("/birthday")
class BirthdayController(
    private val birthdayUsecase: BirthdayUsecase,
    private val birthdayLetterUseCase: BirthdayLetterUseCase,
) {
    @GetMapping("/users")
    @Operation(summary = "생일인 사람들 조회 API")
    fun getBirthdayUser(
        @Parameter(hidden = true) @CurrentUserId loginUserId: Long,
    ): CommonResponse<BirthdayUsersResponse> {
        val response = birthdayUsecase.getTodayBirthdayUsers(loginUserId)

        return CommonResponse.success(ResponseCode.GET_BIRTHDAY_USERS, response)
    }

    @GetMapping("/users/upcoming")
    @Operation(summary = "7일 이내 생일일 사람들 조회 API")
    fun getUpcomingBirthdayUsers(): CommonResponse<UpcomingBirthdayUsersResponse> {
        val response = birthdayUsecase.getUpcomingBirthdayUsers()

        return CommonResponse.success(ResponseCode.GET_UPCOMING_BIRTHDAY_USERS, response)
    }

    @PostMapping("/letters/{receiverId}")
    @Operation(summary = "생일 축하 전송(편지 전송) API")
    fun writeBirthdayLetter(
        @Parameter(hidden = true) @CurrentUserId senderId: Long,
        @PathVariable receiverId: Long,
        @Valid @RequestBody request: BirthdayLetterRequest,
    ): CommonResponse<Void?> {
        birthdayLetterUseCase.writeBirthdayLetter(senderId, receiverId, request)

        return CommonResponse.success(ResponseCode.WRITE_BIRTHDAY_LETTER)
    }

    @GetMapping("/letters/me")
    @Operation(summary = "내가 받은 생일 축하 편지 조회 API")
    fun getMyBirthdayLetters(
        @Parameter(hidden = true) @CurrentUserId receiverId: Long,
    ): CommonResponse<List<MyBirthdayLettersResponse>> {
        val response = birthdayLetterUseCase.getMyBirthdayLetters(receiverId)

        return CommonResponse.success(ResponseCode.GET_MY_BIRTHDAY_LETTERS, response)
    }

    @PostMapping("/letters/me/mark")
    @Operation(summary = "편지 읽음 처리 API")
    fun markMyBirthdayLetters(
        @Parameter(hidden = true) @CurrentUserId loginUserId: Long,
    ): CommonResponse<Void?> {
        birthdayLetterUseCase.markBirthdayLetterRead(loginUserId)

        return CommonResponse.success(ResponseCode.MARK_LETTERS_READ)
    }
}
