package com.freesia.spring.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class FreesiaSpringAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreesiaSpringAdminApplication.class, args);
    }

}
