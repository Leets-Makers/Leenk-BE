package leets.leenk.domain.birthday.application.usecase;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUserResponse;
import leets.leenk.domain.birthday.application.mapper.BirthdayMapper;
import leets.leenk.domain.birthday.domain.service.BirthdayGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayUsecase {
    private final BirthdayMapper birthdayMapper;
    private final BirthdayGetService birthdayGetService;

    @Transactional(readOnly = true)
    public List<BirthdayUserResponse> getTodayBirthdayUsers() {
        LocalDate today = LocalDate.now();

        return birthdayGetService.findTodayBirthdayUsers(today)
                .stream()
                .map(birthdayMapper::toBirthdayUserResponse)
                .toList();
    }
}
