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

    @PersistenceContext
    private EntityManager entityManager;

    private static boolean initialized = false;

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            initialize();
            initialized = true;
            log.info("Application initialization done");
        }
    }

    private void initialize() {
        User u = new User("user", "pass", "user@example.com", true);
        entityManager.persist(u);
    }
}
