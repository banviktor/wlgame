package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the number of uploaded solutions doesn't match the number of words in the room.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidNumberOfSolutionsException extends RuntimeException {}
