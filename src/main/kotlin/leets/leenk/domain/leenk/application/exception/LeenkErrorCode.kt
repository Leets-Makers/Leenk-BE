package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.ErrorCodeInterface
import leets.leenk.global.common.exception.ExplainError
import org.springframework.http.HttpStatus

enum class LeenkErrorCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ErrorCodeInterface {
    @field:ExplainError("링크 ID로 조회했으나 해당 링크가 존재하지 않을 때 발생합니다.")
    LEENK_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "존재하지 않는 링크입니다."),

    @field:ExplainError("링크 상태 ID로 조회했으나 해당 링크 상태가 존재하지 않을 때 발생합니다.")
    LEENK_STATUS_NOT_FOUND(2401, HttpStatus.NOT_FOUND, "존재하지 않는 링크 상태입니다."),

    @field:ExplainError("링크가 모집 중 상태가 아닐 때 참여를 시도하면 발생합니다.")
    LEENK_NOT_RECRUITING(2402, HttpStatus.BAD_REQUEST, "링크가 모집 중 상태가 아닙니다."),

    @field:ExplainError("이미 참여한 링크에 중복 참여를 시도할 때 발생합니다.")
    LEENK_ALREADY_PARTICIPATED(2403, HttpStatus.BAD_REQUEST, "이미 참여한 링크입니다."),

    @field:ExplainError("링크의 최대 참여 인원이 초과된 상태에서 참여를 시도할 때 발생합니다.")
    LEENK_MAX_PARTICIPANTS_EXCEEDED(2404, HttpStatus.BAD_REQUEST, "이미 링크의 최대 참여 인원을 초과했습니다."),

    @field:ExplainError("링크의 방장(작성자)만 수행할 수 있는 작업을 일반 참여자가 시도할 때 발생합니다.")
    LEENK_NOT_OWNER(2405, HttpStatus.FORBIDDEN, "해당 링크의 작성자가(방장) 아닙니다."),

    @field:ExplainError("이미 마감된 링크를 수정하거나 참여하려고 시도할 때 발생합니다.")
    LEENK_ALREADY_CLOSED(2406, HttpStatus.BAD_REQUEST, "이미 마감된 링크입니다."),

    @field:ExplainError("방장이 자기 자신을 링크에서 내보내려고 시도할 때 발생합니다.")
    LEENK_CANNOT_KICK_SELF(2407, HttpStatus.BAD_REQUEST, "방장은 자신을 내보낼 수 없습니다."),

    @field:ExplainError("링크에 참여하지 않은 사용자를 대상으로 작업을 시도할 때 발생합니다.")
    LEENK_PARTICIPANT_NOT_FOUND(2408, HttpStatus.NOT_FOUND, "해당 링크에 참여하지 않은 사용자입니다."),

    @field:ExplainError("방장이 링크를 떠나려고 시도할 때 발생합니다. 방장은 링크를 삭제하거나 방장을 위임해야 합니다.")
    LEENK_CANNOT_LEAVE_AS_HOST(2409, HttpStatus.BAD_REQUEST, "방장은 링크를 떠날 수 없습니다."),

    @field:ExplainError("최대 참여 인원을 현재 참여 인원보다 적은 값으로 수정하려고 시도할 때 발생합니다.")
    MAX_PARTICIPANTS_TOO_LOW(2410, HttpStatus.BAD_REQUEST, "최대 참여 인원은 현재 참여 인원보다 적을 수 없습니다."),

    @field:ExplainError("이미 종료된 링크를 수정하거나 참여하려고 시도할 때 발생합니다.")
    LEENK_ALREADY_FINISHED(2411, HttpStatus.BAD_REQUEST, "이미 종료된 링크입니다."),
}
