package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to change the status of a player in an invalid manner.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidPlayerStateChangeException extends RuntimeException {}
