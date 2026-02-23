package leets.leenk.domain.leenk.domain.entity.enums

enum class LeenkFilter(
    val leenkStatus: LeenkStatus?,
) {
    ALL(null),
    OPEN(LeenkStatus.RECRUITING),
    CLOSED(LeenkStatus.CLOSED),
}
