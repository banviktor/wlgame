package com.viktorban.wlgame.config;

import com.viktorban.wlgame.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class ApplicationInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private Log log = LogFactory.getLog(ApplicationInitializer.class);

    /**
     * EntityManager instance.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Indicates whether initialization has been done.
     */
    private static boolean initialized = false;

    /**
     * Listens for context refresh events, runs initialize() once.
     *
     * @param event The event object.
     */
    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            initialize();
            initialized = true;
            log.info("Application initialization done");
        }
    }

    /**
     * Initializes the application to have some sample data to work with.
     */
    private void initialize() {
        User u = new User("user", "pass", "user@example.com", true);
        entityManager.persist(u);
    }

}
