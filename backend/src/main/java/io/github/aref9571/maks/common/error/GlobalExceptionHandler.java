package io.github.aref9571.maks.common.error;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException exception) {
        return problem(exception.errorCode(), exception.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        List<FieldViolation> errors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.add(new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return problem(ErrorCode.VALIDATION_FAILED, "One or more fields are invalid.", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableBody(HttpMessageNotReadableException exception) {
        return problem(
                ErrorCode.MALFORMED_REQUEST,
                "The request body is missing or not valid JSON.",
                List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return problem(
                ErrorCode.MALFORMED_REQUEST,
                "Parameter '" + exception.getName() + "' has an invalid value.",
                List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleUnknownPath(NoResourceFoundException exception) {
        return problem(ErrorCode.NOT_FOUND, "No resource exists at this path.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
        return problem(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred.", List.of());
    }

    private static ProblemDetail problem(
            ErrorCode errorCode, String detail, List<FieldViolation> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(errorCode.status(), detail);
        problem.setTitle(errorCode.title());
        problem.setProperty("code", errorCode.name());
        problem.setProperty("requestId", "");
        problem.setProperty("errors", errors);
        return problem;
    }
}
