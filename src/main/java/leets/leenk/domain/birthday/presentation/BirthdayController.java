package leets.leenk.domain.birthday.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leets.leenk.domain.birthday.application.dto.response.BirthdayUserResponse;
import leets.leenk.domain.birthday.application.usecase.BirthdayUsecase;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static leets.leenk.domain.birthday.presentation.ResponseCode.GET_BIRTHDAY_USERS;


@Tag(name = "BIRTHDAY")
@RestController
@RequestMapping("/birthday")
@RequiredArgsConstructor
public class BirthdayController {
    private final BirthdayUsecase birthdayUsecase;

    @GetMapping("/users")
    @Operation(summary = "생일인 사람들 조회 API")
    public CommonResponse<List<BirthdayUserResponse>> getBirthdayUser() {
        List<BirthdayUserResponse> response = birthdayUsecase.getTodayBirthdayUsers();

        return CommonResponse.success(GET_BIRTHDAY_USERS, response);
    }
}
