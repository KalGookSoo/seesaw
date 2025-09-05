package kr.me.seesaw.exception;

import kr.me.seesaw.core.validation.ValidationError;
import kr.me.seesaw.message.CmsMessageSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 전역 예외 핸들러
 */
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmsMessageSource messageSource;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.not.found.resource");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
    }

    @ExceptionHandler(value = NoSuchElementException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleNoSuchElementExceptionHtml(NoSuchElementException e) {
        logger.error(e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.constraint.violation");
        return ResponseEntity.badRequest().body(message);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        logger.error(ex.getMessage());
        String message = messageSource.getMessage("error.payload.too.large");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(message);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        logger.error(ex.getMessage());
        String message = messageSource.getMessage("error.unsupported.media.type");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(message);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ValidationError::new)
                .toList();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("errors", errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        logger.error(e.getMessage());
        String message = messageSource.getMessage("error.access.denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler(value = AccessDeniedException.class, produces = MediaType.TEXT_HTML_VALUE)
    public String handleAccessDeniedExceptionHtml(AccessDeniedException e) {
        logger.error(e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(InvalidCsrfTokenException.class)
    public ResponseEntity<String> handleInvalidCsrfTokenException(InvalidCsrfTokenException e) {
        logger.error(e.getMessage());
        return handleAccessDeniedException(e);
    }

}
