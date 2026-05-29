package com.certimaster.shared.advice;

import com.certimaster.shared.exception.BaseException;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.InvalidTokenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.shared.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Centralizes API error responses as RFC 7807 ProblemDetail objects.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_TYPE_PREFIX = "urn:problem-type:";
    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String GLOBAL_ERROR_KEY = "_global";

    @Value("${app.exception.include-stack-trace:false}")
    private boolean includeStackTrace;

    @Value("${app.exception.log-stack-trace:true}")
    private boolean logStackTrace;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} path={}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized request: {} path={}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {
        log.warn("Invalid token: {} code={} path={}", ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {
        log.warn("Forbidden request: {} path={}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(
            BusinessException ex, HttpServletRequest request) {
        HttpStatus status = "DUPLICATE_RESOURCE".equals(ex.getErrorCode())
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        log.warn("Business error: {} code={} path={}", ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
        return build(status, ex, request);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetail> handleBaseException(
            BaseException ex, HttpServletRequest request) {
        log.warn("Application error: {} code={} path={}", ex.getMessage(), ex.getErrorCode(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Bad credentials path={}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(problem(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Invalid username or password", request));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {} path={}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(problem(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
                        "Authentication failed", request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {} path={}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(problem(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                        "Access denied to this resource", request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            addFieldError(fieldErrors, violation.getPropertyPath().toString(), violation.getMessage());
        }

        log.warn("Constraint validation failed fields={} path={}", fieldErrors.keySet(), request.getRequestURI());
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                "Constraint validation failed", request);
        pd.setProperty(FIELD_ERRORS, fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String normalizedMessage = rootCauseMessage(ex).toLowerCase(Locale.ROOT);
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        String message = "Data integrity violation";

        if (containsAny(normalizedMessage, "unique", "duplicate", "duplicate key")) {
            errorCode = "DUPLICATE_RESOURCE";
            message = "This record already exists";
        } else if (containsAny(normalizedMessage, "foreign key", "violates foreign key")) {
            errorCode = "REFERENCE_CONSTRAINT_VIOLATION";
            message = "Cannot perform operation because related records exist";
        } else if (containsAny(normalizedMessage, "not null", "null value")) {
            errorCode = "REQUIRED_FIELD_MISSING";
            message = "Required field is missing";
        }

        log.warn("Data integrity violation code={} path={} message={}", errorCode, request.getRequestURI(), rootCauseMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem(HttpStatus.CONFLICT, errorCode, message, request));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        log.warn("Optimistic locking failure entity={} id={} path={}",
                ex.getPersistentClassName(), ex.getIdentifier(), request.getRequestURI());
        ProblemDetail pd = problem(HttpStatus.CONFLICT, "OPTIMISTIC_LOCK_FAILURE",
                "The record has been modified by another transaction. Please refresh and try again.", request);
        pd.setProperty("entityName", ex.getPersistentClassName());
        pd.setProperty("identifier", Objects.toString(ex.getIdentifier(), null));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> fieldErrors = extractBindingErrors(ex.getBindingResult());
        log.warn("Request validation failed fields={}", fieldErrors.keySet());

        ProblemDetail pd = problem(status, "VALIDATION_ERROR",
                "Validation failed for request", request);
        pd.setProperty(FIELD_ERRORS, fieldErrors);
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(
            BindException ex, HttpServletRequest request) {
        Map<String, List<String>> fieldErrors = extractBindingErrors(ex);
        log.warn("Request binding failed fields={}", fieldErrors.keySet());

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Validation failed for request", request);
        pd.setProperty(FIELD_ERRORS, fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> fieldErrors = extractMethodValidationErrors(ex);
        log.warn("Method validation failed fields={}", fieldErrors.keySet());

        ProblemDetail pd = problem(status, "VALIDATION_ERROR",
                "Validation failed for request", request);
        pd.setProperty(FIELD_ERRORS, fieldErrors);
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Missing request parameter name={} type={}", ex.getParameterName(), ex.getParameterType());
        ProblemDetail pd = problem(status, "MISSING_PARAMETER",
                "Required parameter '" + ex.getParameterName() + "' is missing", request);
        pd.setProperty("parameterName", ex.getParameterName());
        pd.setProperty("parameterType", ex.getParameterType());
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Malformed request body: {}", ex.getMostSpecificCause().getMessage());
        ProblemDetail pd = problem(status, "MALFORMED_REQUEST",
                "Malformed request body", request);
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = problem(status, "TYPE_MISMATCH",
                "Request value has an invalid type", request);

        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            log.warn("Type mismatch parameter={} expected={} provided={}",
                    mismatch.getName(), mismatch.getRequiredType(), mismatch.getValue());
            pd.setDetail("Invalid value for parameter '" + mismatch.getName() + "'");
            pd.setProperty("parameterName", mismatch.getName());
            pd.setProperty("expectedType", mismatch.getRequiredType() != null
                    ? mismatch.getRequiredType().getSimpleName()
                    : "unknown");
            pd.setProperty("providedValue", Objects.toString(mismatch.getValue(), null));
        } else {
            log.warn("Type mismatch property={} requiredType={}", ex.getPropertyName(), ex.getRequiredType());
            pd.setProperty("propertyName", ex.getPropertyName());
            pd.setProperty("expectedType", ex.getRequiredType() != null
                    ? ex.getRequiredType().getSimpleName()
                    : "unknown");
        }

        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("HTTP method not supported method={}", ex.getMethod());
        ProblemDetail pd = problem(status, "METHOD_NOT_SUPPORTED",
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint", request);
        if (ex.getSupportedHttpMethods() != null) {
            headers = new HttpHeaders(headers);
            headers.setAllow(ex.getSupportedHttpMethods());
            pd.setProperty("supportedMethods", ex.getSupportedHttpMethods().stream()
                    .map(HttpMethod::name)
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Unsupported media type contentType={}", ex.getContentType());
        ProblemDetail pd = problem(status, "MEDIA_TYPE_NOT_SUPPORTED",
                "Request content type is not supported", request);
        pd.setProperty("contentType", Objects.toString(ex.getContentType(), null));
        pd.setProperty("supportedMediaTypes", ex.getSupportedMediaTypes());
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Not acceptable media type supported={}", ex.getSupportedMediaTypes());
        ProblemDetail pd = problem(status, "MEDIA_TYPE_NOT_ACCEPTABLE",
                "Requested response media type is not supported", request);
        pd.setProperty("supportedMediaTypes", ex.getSupportedMediaTypes());
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("No handler found method={} url={}", ex.getHttpMethod(), ex.getRequestURL());
        ProblemDetail pd = problem(status, "ENDPOINT_NOT_FOUND",
                "No endpoint found for " + ex.getHttpMethod() + " " + ex.getRequestURL(), request);
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("No resource found method={} path={}", ex.getHttpMethod(), ex.getResourcePath());
        ProblemDetail pd = problem(status, "ENDPOINT_NOT_FOUND",
                "No endpoint found for " + ex.getHttpMethod() + " " + ex.getResourcePath(), request);
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Multipart upload exceeded max size={}", ex.getMaxUploadSize());
        ProblemDetail pd = problem(status, "MAX_UPLOAD_SIZE_EXCEEDED",
                "Uploaded file is too large", request);
        pd.setProperty("maxUploadSize", ex.getMaxUploadSize());
        return ResponseEntity.status(status).headers(headers).body(pd);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Object responseBody = body;
        if (responseBody instanceof ProblemDetail pd) {
            enrich(pd, defaultErrorCode(statusCode), request);
        } else if (responseBody == null) {
            responseBody = problem(statusCode, defaultErrorCode(statusCode),
                    defaultDetail(statusCode), request);
        }
        return ResponseEntity.status(statusCode).headers(headers).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, HttpServletRequest request) {
        if (logStackTrace) {
            log.error("Unexpected error path={}", request.getRequestURI(), ex);
        } else {
            log.error("Unexpected error path={} message={}", request.getRequestURI(), ex.getMessage());
        }

        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.", request);
        if (includeStackTrace) {
            pd.setProperty("exception", ex.getClass().getName());
            pd.setProperty("stackTrace", Arrays.stream(ex.getStackTrace())
                    .limit(10)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, BaseException ex, HttpServletRequest request) {
        ProblemDetail pd = problem(status, ex.getErrorCode(), ex.getMessage(), request);
        pd.setProperty("exceptionTimestamp", ex.getTimestamp().toString());
        if (ex.getDetails() != null && !ex.getDetails().isEmpty()) {
            pd.setProperty("details", ex.getDetails());
        }
        return ResponseEntity.status(status).body(pd);
    }

    private ProblemDetail problem(HttpStatusCode status, String errorCode, String detail, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        enrich(pd, errorCode, request);
        return pd;
    }

    private ProblemDetail problem(HttpStatus status, String errorCode, String detail, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        enrich(pd, errorCode, request);
        return pd;
    }

    private void enrich(ProblemDetail pd, String errorCode, WebRequest request) {
        pd.setType(toTypeUri(errorCode));
        pd.setTitle(resolveTitle(pd.getStatus(), errorCode));
        pd.setProperty("errorCode", errorCode);
        pd.setProperty("timestamp", Instant.now().toString());
        addCorrelationId(pd);

        if (request instanceof ServletWebRequest swr) {
            HttpServletRequest servletRequest = swr.getRequest();
            pd.setInstance(URI.create(servletRequest.getRequestURI()));
            pd.setProperty("method", servletRequest.getMethod());
        }
    }

    private void enrich(ProblemDetail pd, String errorCode, HttpServletRequest request) {
        pd.setType(toTypeUri(errorCode));
        pd.setTitle(resolveTitle(pd.getStatus(), errorCode));
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("errorCode", errorCode);
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("method", request.getMethod());
        addCorrelationId(pd);
    }

    private void addCorrelationId(ProblemDetail pd) {
        String correlationId = MDC.get("correlationId");
        if (correlationId != null && !correlationId.isBlank()) {
            pd.setProperty("correlationId", correlationId);
        }
    }

    private Map<String, List<String>> extractBindingErrors(BindingResult bindingResult) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                addFieldError(fieldErrors, fieldError.getField(), defaultMessage(fieldError));
            } else {
                addFieldError(fieldErrors, GLOBAL_ERROR_KEY, defaultMessage(error));
            }
        }
        return fieldErrors;
    }

    private Map<String, List<String>> extractMethodValidationErrors(HandlerMethodValidationException ex) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String parameterName = parameterName(result);
            if (result instanceof ParameterErrors parameterErrors) {
                for (FieldError fieldError : parameterErrors.getFieldErrors()) {
                    addFieldError(fieldErrors, parameterName + "." + fieldError.getField(), defaultMessage(fieldError));
                }
                for (ObjectError objectError : parameterErrors.getGlobalErrors()) {
                    addFieldError(fieldErrors, parameterName, defaultMessage(objectError));
                }
            } else {
                for (MessageSourceResolvable error : result.getResolvableErrors()) {
                    addFieldError(fieldErrors, parameterName, defaultMessage(error));
                }
            }
        }
        return fieldErrors;
    }

    private void addFieldError(Map<String, List<String>> errors, String field, String message) {
        errors.computeIfAbsent(field, ignored -> new java.util.ArrayList<>()).add(message);
    }

    private String parameterName(ParameterValidationResult result) {
        String parameterName = result.getMethodParameter().getParameterName();
        if (parameterName != null && !parameterName.isBlank()) {
            return parameterName;
        }
        return "arg" + result.getMethodParameter().getParameterIndex();
    }

    private String defaultMessage(MessageSourceResolvable error) {
        return error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
    }

    private String rootCauseMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : ex.getMessage();
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private URI toTypeUri(String errorCode) {
        return URI.create(PROBLEM_TYPE_PREFIX + errorCode.toLowerCase(Locale.ROOT).replace('_', '-'));
    }

    private String resolveTitle(int statusCode, String errorCode) {
        HttpStatus status = HttpStatus.resolve(statusCode);
        return status != null ? status.getReasonPhrase() : errorCode;
    }

    private String defaultErrorCode(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        if (status == null) {
            return "HTTP_" + statusCode.value();
        }
        return status.name();
    }

    private String defaultDetail(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null ? status.getReasonPhrase() : "Request failed";
    }
}
