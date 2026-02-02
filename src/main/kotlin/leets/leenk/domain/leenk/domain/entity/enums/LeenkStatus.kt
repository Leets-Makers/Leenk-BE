package leets.leenk.domain.leenk.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeenkStatus {
    RECRUITING("모집중"),
    CLOSED("모집 완료"),
    FINISHED("모임 종료");

    private final String displayValue;
}
