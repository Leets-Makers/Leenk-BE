package leets.leenk.global.common.exception

import leets.leenk.global.common.exception.response.ValidErrorResponse
import leets.leenk.global.common.response.CommonResponse
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BaseException::class)
    fun handleException(e: BaseException): ResponseEntity<CommonResponse<Void?>> {
        val errorCode = e.errorCode
        val errorMessage = e.message ?: errorCode.message
        val body = CommonResponse.error(errorCode, errorMessage)

        return ResponseEntity
            .status(errorCode.status)
            .body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<CommonResponse<List<ValidErrorResponse>>> {
        val commonErrorCode = CommonErrorCode.INVALID_ARGUMENT
        val errors =
            e.bindingResult.fieldErrors.map {
                ValidErrorResponse.of(it.field, it.defaultMessage ?: "Validation failed", it.rejectedValue)
            }

        return ResponseEntity
            .status(commonErrorCode.status)
            .body(CommonResponse.error(commonErrorCode, errors))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.INVALID_ARGUMENT.let { errorCode ->
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(e: NoResourceFoundException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.RESOURCE_NOT_FOUND.let { errorCode ->
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.METHOD_NOT_ALLOWED.let { errorCode ->
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<CommonResponse<Void?>> =
        when (val cause = ex.mostSpecificCause) {
            is BaseException -> {
                ResponseEntity
                    .status(cause.errorCode.status)
                    .body(CommonResponse.error(cause.errorCode, ex.message ?: cause.errorCode.message))
            }

            else -> {
                val commonErrorCode = CommonErrorCode.JSON_PARSE_ERROR
                ResponseEntity
                    .status(commonErrorCode.status)
                    .body(CommonResponse.error(commonErrorCode, ex.message ?: commonErrorCode.message))
            }
        }

    @ExceptionHandler(Exception::class)
    fun handleAll(e: Exception): ResponseEntity<CommonResponse<Void?>> {
        val commonErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val body = CommonResponse.error(commonErrorCode, e.message ?: commonErrorCode.message)

        return ResponseEntity
            .status(commonErrorCode.status)
            .body(body)
    }
}
