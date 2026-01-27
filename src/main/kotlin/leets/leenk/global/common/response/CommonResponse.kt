package leets.leenk.global.common.response

import leets.leenk.global.common.exception.ErrorCodeInterface

data class CommonResponse<T>(
    val code: Int,
    val message: String,
    val data: T?,
) {
    companion object {
        @JvmStatic
        fun success(responseCode: ResponseCodeInterface): CommonResponse<Void?> =
            CommonResponse(
                code = responseCode.code,
                message = responseCode.message,
                data = null,
            )

        @JvmStatic
        fun <T> success(
            responseCode: ResponseCodeInterface,
            data: T,
        ): CommonResponse<T> =
            CommonResponse(
                code = responseCode.code,
                message = responseCode.message,
                data = data,
            )

        @JvmStatic
        fun error(errorCode: ErrorCodeInterface): CommonResponse<Void?> =
            CommonResponse(
                code = errorCode.code,
                message = errorCode.message,
                data = null,
            )

        @JvmStatic
        fun error(
            errorCode: ErrorCodeInterface,
            message: String,
        ): CommonResponse<Void?> =
            CommonResponse(
                code = errorCode.code,
                message = message,
                data = null,
            )

        @JvmStatic
        fun <T> error(
            errorCode: ErrorCodeInterface,
            data: T,
        ): CommonResponse<T> =
            CommonResponse(
                code = errorCode.code,
                message = errorCode.message,
                data = data,
            )
    }
}
