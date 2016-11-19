package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the action is not applicable given the current state of the room.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidRoomActionException extends RuntimeException {}
