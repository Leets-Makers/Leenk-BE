package leets.leenk.domain.leenk.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeenkFilter {
    ALL("전체"),
    OPEN("모집중"),
    CLOSED("모집완료");

    private final String displayValue;
}
