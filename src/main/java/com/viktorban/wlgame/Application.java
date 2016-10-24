package com.viktorban.wlgame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application {

    private static ApplicationContext context;

    @Autowired
    public Application(ApplicationContext context) {
        Application.context = context;
    }

    public static PasswordEncoder getPasswordEncoder() {
        return (PasswordEncoder) context.getBean("passwordEncoder");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
