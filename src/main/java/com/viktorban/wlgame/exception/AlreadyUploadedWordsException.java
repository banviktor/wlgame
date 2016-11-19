package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to upload words if the user has already uploaded words.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AlreadyUploadedWordsException extends RuntimeException {}
