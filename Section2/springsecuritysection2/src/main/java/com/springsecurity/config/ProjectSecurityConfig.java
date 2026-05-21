package com.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // It will require authentication for all request
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());*/

        //It will permit all request without authentication
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());*/

        //It will deny all request even if user is authenticated
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());*/

        //Here few will be required authentication & few are not
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact", "/error").permitAll());
        //Default authentication login page where we can enter credentials
        http.formLogin(withDefaults());

        // It will diable form login authentication
        //http.formLogin(flc -> flc.disable());

        //Http basic authentication which will pass credential in basic 64 encoded form like Basic ADSGYUH876
        http.httpBasic(withDefaults());

        //It will disable http basic authentication
       // http.httpBasic(hbc-> hbc.disable());
        return http.build();
    }
}
