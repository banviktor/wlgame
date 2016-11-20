package com.viktorban.wlgame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the action is invoked by a player not part of the room.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PlayerNotPartOfRoomException extends RuntimeException {}
