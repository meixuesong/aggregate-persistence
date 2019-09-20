package com.github.meixuesong.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionMapperHandler extends ResponseEntityExceptionHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception exception) {
        logger.error("ResponseException exception: {}", exception);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({EntityNotFoundException.class, JpaObjectRetrievalFailureException.class})
    public ResponseEntity handleSizeExceededException(HttpServletRequest request, EntityNotFoundException exception) {
        logger.error("EntityNotFoundException exception: {}", exception);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
