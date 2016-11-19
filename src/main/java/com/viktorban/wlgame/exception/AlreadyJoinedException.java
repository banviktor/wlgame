package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to join a room the user is already in.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AlreadyJoinedException extends RuntimeException {}
