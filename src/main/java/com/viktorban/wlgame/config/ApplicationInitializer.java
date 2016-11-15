package com.viktorban.wlgame.config;

import com.viktorban.wlgame.model.Language;
import com.viktorban.wlgame.model.Role;
import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.Word;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Class for application initialization.
 */
@Component
public class ApplicationInitializer implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Logger object.
     */
    private Log log = LogFactory.getLog(ApplicationInitializer.class);

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Indicates whether initialization has been done.
     */
    private static boolean initialized = false;

    /**
     * Listens to 'ContextRefreshedEvent's.
     *
     * @param event The event.
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
     * Does the application's initialization.
     */
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
        moderator.addRole(rolePlayer);
        moderator.addRole(roleModerator);
        entityManager.persist(moderator);

        // Create administrator.
        User administrator = new User("admin", "admin", "admin@example.com", true);
        administrator.addRole(rolePlayer);
        administrator.addRole(roleModerator);
        administrator.addRole(roleAdministrator);
        entityManager.persist(administrator);

        // Create languages.
        Language langEnglish = new Language("en", "english");
        entityManager.persist(langEnglish);
        Language langHungarian = new Language("hu", "hungarian");
        entityManager.persist(langHungarian);

        // Create some test words.
        Word wEnCat = new Word(langEnglish, "cat");
        Word wHuCat1 = new Word(langHungarian, "macska");
        Word wHuCat2 = new Word(langHungarian, "cica");
        entityManager.persist(wHuCat1);
        entityManager.persist(wHuCat2);
        wEnCat.addTranslation(wHuCat1);
        wEnCat.addTranslation(wHuCat2);
        entityManager.persist(wEnCat);
    }

}
