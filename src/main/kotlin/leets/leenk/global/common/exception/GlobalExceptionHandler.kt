package leets.leenk.global.common.exception

import leets.leenk.global.common.exception.response.ValidErrorResponse
import leets.leenk.global.common.response.CommonResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleException(e: BaseException): ResponseEntity<CommonResponse<Void?>> {
        val errorCode = e.errorCode
        val errorMessage = e.message ?: errorCode.message
        val body = CommonResponse.error(errorCode, errorMessage)

        log.warn("BaseException handled: code={}, message={}", errorCode.code, errorMessage, e)

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

        log.warn("Validation exception handled: errors={}", errors, e)

        return ResponseEntity
            .status(commonErrorCode.status)
            .body(CommonResponse.error(commonErrorCode, errors))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.INVALID_ARGUMENT.let { errorCode ->
            log.warn("IllegalArgumentException handled: message={}", e.message, e)
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(e: NoResourceFoundException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.RESOURCE_NOT_FOUND.let { errorCode ->
            log.warn("NoResourceFoundException handled: message={}", e.message, e)
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<CommonResponse<Void?>> =
        CommonErrorCode.METHOD_NOT_ALLOWED.let { errorCode ->
            log.warn("HttpRequestMethodNotSupportedException handled: method={}, message={}", e.method, e.message, e)
            ResponseEntity.status(errorCode.status).body(CommonResponse.error(errorCode))
        }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<CommonResponse<Void?>> =
        when (val cause = ex.mostSpecificCause) {
            is BaseException -> {
                log.warn(
                    "HttpMessageNotReadableException handled by BaseException: code={}, message={}",
                    cause.errorCode.code,
                    ex.message,
                    ex,
                )
                ResponseEntity
                    .status(cause.errorCode.status)
                    .body(CommonResponse.error(cause.errorCode, ex.message ?: cause.errorCode.message))
            }

            else -> {
                val commonErrorCode = CommonErrorCode.JSON_PARSE_ERROR
                log.warn("HttpMessageNotReadableException handled: message={}", ex.message, ex)
                ResponseEntity
                    .status(commonErrorCode.status)
                    .body(CommonResponse.error(commonErrorCode, ex.message ?: commonErrorCode.message))
            }
        }

    @ExceptionHandler(Exception::class)
    fun handleAll(e: Exception): ResponseEntity<CommonResponse<Void?>> {
        val commonErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val body = CommonResponse.error(commonErrorCode, e.message ?: commonErrorCode.message)

        log.error("Unhandled exception handled: message={}", e.message, e)

        return ResponseEntity
            .status(commonErrorCode.status)
            .body(body)
    }
}
