package leets.leenk.domain.user.application.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record AgreementRequest(
        @NotNull @AssertTrue(message = "서비스 이용 약관에 동의해주세요") boolean termsService,
        @NotNull @AssertTrue(message = "개인정보 수집에 동의해주세요") boolean privacyPolicy
) {
}
