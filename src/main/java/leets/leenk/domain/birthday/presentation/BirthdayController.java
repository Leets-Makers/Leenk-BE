package leets.leenk.domain.birthday.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse;
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse;
import leets.leenk.domain.birthday.application.exception.BirthdayErrorCode;
import leets.leenk.domain.birthday.application.usecase.BirthdayLetterUseCase;
import leets.leenk.domain.birthday.application.usecase.BirthdayUsecase;
import leets.leenk.domain.user.application.exception.UserErrorCode;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.exception.ApiErrorCodeExample;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BIRTHDAY")
@ApiErrorCodeExample({BirthdayErrorCode.class,  UserErrorCode.class})
@RestController
@RequestMapping("/birthday")
@RequiredArgsConstructor
public class BirthdayController {
    private final BirthdayUsecase birthdayUsecase;
    private final BirthdayLetterUseCase birthdayLetterUseCase;

    @GetMapping("/users")
    @Operation(summary = "생일인 사람들 조회 API")
    public CommonResponse<BirthdayUsersResponse> getBirthdayUser(
            @Parameter(hidden = true) @CurrentUserId Long loginUserId) {
        BirthdayUsersResponse response = birthdayUsecase.getTodayBirthdayUsers(loginUserId);

        return CommonResponse.success(ResponseCode.GET_BIRTHDAY_USERS, response);
    }

    @GetMapping("/users/upcoming")
    @Operation(summary = "7일 이내 생일일 사람들 조회 API")
    public CommonResponse<UpcomingBirthdayUsersResponse> getBirthdayUser() {
        UpcomingBirthdayUsersResponse response = birthdayUsecase.getUpcomingBirthdayUsers();

        return CommonResponse.success(ResponseCode.GET_UPCOMING_BIRTHDAY_USERS, response);
    }

    @PostMapping("/letters/{receiverId}")
    @Operation(summary = "생일 축하 전송(편지 전송) API")
    public CommonResponse<Void> writeBirthdayLetter(@Parameter(hidden = true) @CurrentUserId Long senderId,
                                                    @PathVariable long receiverId,
                                                    @Valid @RequestBody BirthdayLetterRequest request) {
        birthdayLetterUseCase.writeBirthdayLetter(senderId, receiverId, request);

        return CommonResponse.success(ResponseCode.WRITE_BIRTHDAY_LETTER);
    }

    @GetMapping("/letters/me")
    @Operation(summary = "내가 받은 생일 축하 편지 조회 API")
    public CommonResponse<List<MyBirthdayLettersResponse>> getMyBirthdayLetters(
            @Parameter(hidden = true) @CurrentUserId Long receiverId) {
        List<MyBirthdayLettersResponse> response = birthdayLetterUseCase.getMyBirthdayLetters(receiverId);

        return CommonResponse.success(ResponseCode.GET_MY_BIRTHDAY_LETTERS, response);
    }



    @PostMapping("/letters/me/mark")
    @Operation(summary = "편지 읽음 처리 API")
    public CommonResponse<Void> markMyBirthdayLetters(@Parameter(hidden = true) @CurrentUserId Long loginUserId) {
        birthdayLetterUseCase.markBirthdayLetterRead(loginUserId);

        return CommonResponse.success(ResponseCode.MARK_LETTERS_READ);
    }
}
