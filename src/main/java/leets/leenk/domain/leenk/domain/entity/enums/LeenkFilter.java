package leets.leenk.domain.leenk.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeenkFilter {
    ALL(null),
    OPEN(LeenkStatus.RECRUITING),
    CLOSED(LeenkStatus.CLOSED);

    private final LeenkStatus leenkStatus;
}
