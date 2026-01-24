package leets.leenk.global.common.exception;

import leets.leenk.global.common.exception.response.ValidErrorResponse;
import leets.leenk.global.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Void>> handleException(BaseException e) {
        ErrorCodeInterface errorCode = e.getErrorCode();
        String errorMessage = e.getMessage();
        CommonResponse<Void> body = CommonResponse.error(errorCode, errorMessage);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<List<ValidErrorResponse>>> handleValidation(MethodArgumentNotValidException e) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT;

        List<ValidErrorResponse> errors = e.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> ValidErrorResponse.of(
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue()
                ))
                .toList();
        CommonResponse<List<ValidErrorResponse>> body =
                CommonResponse.error(errorCode, errors);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT;
        CommonResponse<Void> body = CommonResponse.error(errorCode);


        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleNoResourceFound() {
        CommonErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;
        CommonResponse<Void> body = CommonResponse.error(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        CommonErrorCode errorCode = CommonErrorCode.METHOD_NOT_ALLOWED;
        CommonResponse<Void> body = CommonResponse.error(errorCode);


        return ResponseEntity
                .status(e.getStatusCode().value())
                .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof BaseException be) {
            ErrorCodeInterface errorCode = be.getErrorCode();
            CommonResponse<Void> body = CommonResponse.error(errorCode, ex.getMessage());

            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(body);
        }

        CommonErrorCode errorCode = CommonErrorCode.JSON_PARSE_ERROR;
        CommonResponse<Void> body = CommonResponse.error(errorCode, ex.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAll(Exception e) {
        CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        CommonResponse<Void> body = CommonResponse.error(errorCode, e.getMessage());


        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body);
    }
}
