package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic exception thrown for bad requests.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public BadRequestException(String message) {
        super(message);
    }

}
