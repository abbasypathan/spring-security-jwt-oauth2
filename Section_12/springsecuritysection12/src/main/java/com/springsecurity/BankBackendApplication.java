package com.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)//Enables debug log for spring security related flow
//It will enable method level security like @PreAuthorize, @PostAuthorize, @Secured etc
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class BankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankBackendApplication.class, args);
    }
}