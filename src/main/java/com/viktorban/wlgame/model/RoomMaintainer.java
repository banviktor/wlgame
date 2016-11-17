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

@Component
public class RoomMaintainer {

    /**
     * Logger object.
     */
    private Log log = LogFactory.getLog(RoomMaintainer.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedDelay = 5000L)
    @Transactional
    public void timeoutRooms() {
        List<Room> rooms = entityManager.createQuery("SELECT r FROM com.viktorban.wlgame.model.Room r WHERE r.state <> 'ENDED'").getResultList();
        Date now = new Date();
        for (Room room : rooms) {
            if ((room.getState() == Room.RoomState.WAITING_FOR_PLAYERS && now.getTime() > room.getOpened().getTime() + Room.timeoutJoin) ||
                    (room.getState() == Room.RoomState.WAITING_FOR_WORDS && now.getTime() > room.getJoined().getTime() + Room.timeoutUploadWords) ||
                    (room.getState() == Room.RoomState.IN_PROGRESS && now.getTime() > room.getStarted().getTime() + Room.timeoutUploadSolutions)) {
                room.setState(Room.RoomState.ENDED);
                room.setTimedOut(true);
                log.info("Room " + room.getRoomId() + " timed out.");
            }
        }
        entityManager.flush();
    }

}
