package com.viktorban.wlgame.config;

import com.viktorban.wlgame.model.Role;
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
        // Create roles.
        Role rolePlayer = new Role("PLAYER");
        entityManager.persist(rolePlayer);
        Role roleModerator = new Role("MODERATOR");
        entityManager.persist(roleModerator);
        Role roleAdministrator = new Role("ADMINISTRATOR");
        entityManager.persist(roleAdministrator);

        // Create player.
        User player = new User("user", "user", "user@example.com", true);
        player.addRole(rolePlayer);
        entityManager.persist(player);

        // Create moderator.
        User moderator = new User("mod", "mod", "mod@example.com", true);
        moderator.addRole(rolePlayer).addRole(roleModerator);
        entityManager.persist(moderator);

        // Create administrator.
        User administrator = new User("admin", "admin", "admin@example.com", true);
        administrator.addRole(rolePlayer).addRole(roleModerator).addRole(roleAdministrator);
        entityManager.persist(administrator);
    }
}
