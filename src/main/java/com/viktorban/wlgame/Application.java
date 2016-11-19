package com.viktorban.wlgame;

import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Represents the application and contains the entry point.
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    /**
     * The application context.
     */
    private static ApplicationContext context;

    /**
     * Application constructor.
     *
     * @param context The application context.
     */
    @Autowired
    public Application(ApplicationContext context) {
        Application.context = context;
    }

    /**
     * Returns the password encoder bean.
     *
     * @return The password encoder bean.
     */
    public static PasswordEncoder getPasswordEncoder() {
        return (PasswordEncoder) context.getBean("passwordEncoder");
    }

    /**
     * The application entry point.
     *
     * @param args Array of arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Returns the logged in user.
     *
     * @return The logged in user.
     */
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserWrapper) {
            UserWrapper userWrapper = (UserWrapper) principal;
            return userWrapper.getUser();
        }
        return null;
    }

    /**
     * Logs the user in.
     *
     * @param user The user to log in.
     */
    public static void forceLogin(User user) {
        UserWrapper userWrapper = new UserWrapper(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userWrapper, null, userWrapper.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
