package com.surefor.service.api;


import com.surefor.service.common.exception.TMSPlatformException;
import com.surefor.service.common.monitor.MetricsHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Gets the current request from spring boot RequestContextHolder.
     * @see <a href="https://github.com/Baconlandia/logme2/blob/master/src/main/java/com/agilemobiledeveloper/logme/LogController.java">RequestContextHolder
     *
     * @return Current Request in progress
     */
    public static Optional<HttpServletRequest> getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(Objects.isNull(requestAttributes)) {
            return Optional.empty();
        }

        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        if(Objects.isNull(servletRequest)) {
            return Optional.empty();
        }

        return Optional.of(servletRequest);
    }

    /**
     * Automatically extract the request id, add it to api error and log
     *
     * @param apiError the api error to be logged
     * @return Same api error that is passed to allow method chaining
     */
    static ApiError logApiError(final ApiError apiError) {
        Optional<HttpServletRequest> httpRequest = getCurrentRequest();

        if (httpRequest.isPresent()) {
            apiError.setRequestId(getCurrentRequest().get().getHeader("request-id"));
        }

        log.error(apiError.toString());
        return apiError;
    }

    /**
     * Automatically extract the request id, add it to api error and log
     *
     * @param apiError the api error to be logged
     * @param logMessage additional log line(s)
     * @return Same api error that is passed to allow method chaining
     */
    static ApiError logApiError(final ApiError apiError, final String logMessage) {
        Optional<HttpServletRequest> httpRequest = getCurrentRequest();

        if (httpRequest.isPresent()) {
            apiError.setRequestId(getCurrentRequest().get().getHeader("request-id"));
        }

        log.error(apiError.toString());

        if(Objects.nonNull(logMessage)) {
            log.error(logMessage);
        }

        return apiError;
    }

    ///	protected ResponseEntity<Object> handleMethodArgumentNotValid(
    //			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex the MethodArgumentNotValidException that is thrown when @Valid validation fails
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        final List<String> errors = new ArrayList<String>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        apiError.setMessage(MessageFormat.format("Validation error:  {0}", errors.toString()));
        logApiError(apiError);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(TMSPlatformException.class)
    protected ResponseEntity<Object> handleTMSPlatformException(TMSPlatformException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.valueOf(ex.getHttpStatusCode()), ex.getMessage())));
    }


    /**
     * Handle javax.persistence.EntityNotFoundException
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage())));
    }


    /**
     * Handle Exception, handle generic IllegalArgumentException.class
     *
     * @param ex the Exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage())));
    }

    /**
     * Handle Exception, handle generic IllegalArgumentException.class
     *
     * @param ex the Exception
     */
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex.getLocalizedMessage())));
    }

    /**
     * handles generic JPA/Hibernate JDBCException
     * @param ex generic Hibernate exception
     */
    @ExceptionHandler(JDBCException.class)
    protected ResponseEntity<Object> handleJPAJDBCException(JDBCException ex) {
        String logMessage = MessageFormat.format("{0} {1} \n{2}}",
                ex.getSQLState(), ex.getSQLException().getMessage(), ex.getSQLException().toString());
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()), logMessage));
    }

    /**
     * handles generic JPA/Hibernate exception
     * @param ex generic Hibernate exception
     */
    @ExceptionHandler(PersistenceException.class)
    protected ResponseEntity<Object> handleJPAPersistenceException(PersistenceException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage())));
    }

    /**
     * handles generic RuntimeException
     */    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage())));
    }

    /**
     * handles generic RuntimeException
     */    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {
        return buildResponseEntity(logApiError(new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex.getLocalizedMessage())));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
