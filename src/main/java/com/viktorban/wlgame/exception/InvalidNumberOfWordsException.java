package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the number of uploaded words doesn't match the expected number of words.
 *
 * @see com.viktorban.wlgame.model.Room#wordsPerPlayer
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidNumberOfWordsException extends RuntimeException {}
