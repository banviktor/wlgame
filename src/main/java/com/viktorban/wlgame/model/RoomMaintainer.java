package com.viktorban.wlgame.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Maintains rooms using scheduled tasks.
 *
 * @see com.viktorban.wlgame.model.Room
 */
@Component
public class RoomMaintainer {

    /**
     * Logger object.
     */
    private static Log log = LogFactory.getLog(RoomMaintainer.class);

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Loops through open rooms and closes timed out ones.
     */
    @Scheduled(fixedDelay = 5000L)
    @Transactional
    public void timeoutRooms() {
        List<Room> rooms = entityManager.createQuery("SELECT r FROM com.viktorban.wlgame.model.Room r WHERE r.state <> 'ENDED'").getResultList();
        long now = (new Date()).getTime();
        boolean changed = false;
        for (Room room : rooms) {
            changed = true;
            boolean timeoutJoin = room.getState() == Room.RoomState.WAITING_FOR_PLAYERS && now > room.getOpened().getTime() + Room.timeoutJoin;
            boolean timeoutUploadWords = room.getState() == Room.RoomState.WAITING_FOR_WORDS && now > room.getJoined().getTime() + Room.timeoutUploadWords;
            boolean timeoutUploadSolutions = room.getState() == Room.RoomState.IN_PROGRESS && now > room.getStarted().getTime() + Room.timeoutUploadSolutions;
            if (timeoutJoin || timeoutUploadWords) {
                room.setState(Room.RoomState.ENDED);
                room.setTimedOut(true);
                log.info(room.toString() + " timed out.");
            } else if (timeoutUploadSolutions) {
                // Time out players that didn't upload their solutions.
                room.getRoomPlayers().stream().filter(roomPlayer -> roomPlayer.getState() != RoomPlayer.RoomPlayerState.DONE).forEach(roomPlayer -> roomPlayer.setState(RoomPlayer.RoomPlayerState.TIMED_OUT));
                room.setState(Room.RoomState.ENDED);
            }
            // Move players out from Memorizing status to Solving.
            for (RoomPlayer roomPlayer : room.getRoomPlayers()) {
                if (roomPlayer.getState() == RoomPlayer.RoomPlayerState.MEMORIZING && now > roomPlayer.getStartedMemorizing().getTime() + RoomPlayer.timeoutMemorize) {
                    roomPlayer.setState(RoomPlayer.RoomPlayerState.SOLVING);
                }
            }
        }
        if (changed) {
            entityManager.flush();
        }
    }

}
