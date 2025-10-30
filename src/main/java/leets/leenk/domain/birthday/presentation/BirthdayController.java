package leets.leenk.domain.birthday.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.birthday.application.dto.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.BirthdayUserResponse;
import leets.leenk.domain.birthday.application.usecase.BirthdayLetterUseCase;
import leets.leenk.domain.birthday.application.usecase.BirthdayUsecase;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BIRTHDAY")
@RestController
@RequestMapping("/birthday")
@RequiredArgsConstructor
public class BirthdayController {
    private final BirthdayUsecase birthdayUsecase;
    private final BirthdayLetterUseCase birthdayLetterUseCase;

    @GetMapping("/users")
    @Operation(summary = "생일인 사람들 조회 API")
    public CommonResponse<List<BirthdayUserResponse>> getBirthdayUser() {
        List<BirthdayUserResponse> response = birthdayUsecase.getTodayBirthdayUsers();

        return CommonResponse.success(ResponseCode.GET_BIRTHDAY_USERS, response);
    }

    @PostMapping("/letters/{recipientId}")
    @Operation(summary = "생일 축하 전송(편지 전송) API")
    public CommonResponse<Void> writeBirthdayLetter(@Parameter(hidden = true) @CurrentUserId Long senderId,
                                                    @PathVariable long recipientId,
                                                    @Valid @RequestBody BirthdayLetterRequest request) {
        birthdayLetterUseCase.writeBirthdayLetter(senderId, recipientId, request);

        return CommonResponse.success(ResponseCode.WRITE_BIRTHDAY_LETTER);
    }
}
