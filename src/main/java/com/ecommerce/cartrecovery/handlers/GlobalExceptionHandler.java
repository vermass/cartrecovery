package com.ecommerce.cartrecovery.handlers;

import com.ecommerce.cartrecovery.enums.ErrorCode;
import com.ecommerce.cartrecovery.exceptions.CartNotFoundException;
import com.ecommerce.cartrecovery.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle CartRecoveryException and its subclasses
     */
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCartException(
            CartNotFoundException ex,
            WebRequest request) {

        logger.warn("Invalid cart exception has occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getDetails()
        );

        HttpStatus status = getHttpStatus(ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Map custom error codes to HTTP status codes
     */
    private HttpStatus getHttpStatus(ErrorCode errorCode) {

        switch (errorCode) {

            case CART_NOT_FOUND:
                return HttpStatus.BAD_REQUEST;

            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}

