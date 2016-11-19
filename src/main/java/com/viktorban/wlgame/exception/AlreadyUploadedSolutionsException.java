package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to upload solutions if the user has already uploaded solutions.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AlreadyUploadedSolutionsException extends RuntimeException {}
