package com.wearewaes.scalableweb.controller;

import com.wearewaes.scalableweb.exception.BadRequestException;
import com.wearewaes.scalableweb.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains methods that declare {@link ExceptionHandler}
 * to be shared across multiple {@code @Controller} classes.
 */
@Slf4j
@ControllerAdvice
@RestController
public class RestExceptionHandler {

    /**
     * Handles {@link MethodArgumentNotValidException} instances for validating purposes.
     *
     * @param ex {@link MethodArgumentNotValidException} object to handle.
     * @return {@link Map} with field name to error message for each error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error("Validation exception. Reason: [{}]", ex.getLocalizedMessage());

        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(error -> ((FieldError) error).getField(), ObjectError::getDefaultMessage));
    }

    /**
     * Puts 400 Bad Request into logs.
     *
     * @param ex {@link BadRequestException}.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public void badRequestException(BadRequestException ex) {
        log.error("Bad request. Reason: [{}]", ex.getLocalizedMessage());
    }

    /**
     * Puts 404 Bad Request into logs.
     *
     * @param ex {@link NotFoundException}.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void notFoundException(NotFoundException ex) {
        log.error("Not Found. Reason: [{}]", ex.getLocalizedMessage());
    }
}
