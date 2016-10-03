package com.viktorban.wlgame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    private static ApplicationContext context;

    @Autowired
    public Application(ApplicationContext context) {
        Application.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
