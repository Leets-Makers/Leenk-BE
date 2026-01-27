package leets.leenk.global.common.exception

abstract class BaseException
    @JvmOverloads
    constructor(
        val errorCode: ErrorCodeInterface,
        message: String? = null,
    ) : RuntimeException(message ?: errorCode.message)
