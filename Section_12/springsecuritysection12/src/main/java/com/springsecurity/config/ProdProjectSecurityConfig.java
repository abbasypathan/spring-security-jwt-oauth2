package com.springsecurity.config;

import com.springsecurity.exceptionhandling.CustomAccessDeniedHandler;
import com.springsecurity.exceptionhandling.CustomBasicAuthEntryPoint;
import com.springsecurity.filter.*;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("prod")
public class ProdProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        // It will require authentication for all request
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());*/

        //It will permit all request without authentication
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());*/

        //It will deny all request even if user is authenticated
        /*http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll());*/

        //Here few will be required authentication & few are not
        // Using http.csrf() we are disable CSRF restriction
        //It is only accepting HTTPS request (deprecated)
        //http.requiresChannel(channel -> channel.anyRequest().requiresSecure())
        //http.redirectToHttps(Customizer.withDefaults())

        //we can use different session fixation strategy which spring security provides
        //http.sessionManagement(session -> session.sessionFixation(sfc -> sfc.none())


        http
                //This is required when we use JSESSIONID
                //.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
                // ALWAYS will generate session id
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //http.cors() We can add CORS config globally for all controller
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowCredentials(true);
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Collections.singletonList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                //We can redirect to another page when session timeout
                //.sessionManagement(session -> session.invalidSessionUrl("/invalidSession").maximumSessions(3).maxSessionsPreventsLogin(true))
                //.csrf(csrfConfig -> csrfConfig.disable())
                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/contact", "/register","/apiLogin")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        //.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
//                        .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
//                        .requestMatchers("/myBalance").hasAnyAuthority("VIEWBALANCE", "VIEWACCOUNT")
//                        .requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
//                        .requestMatchers("/myCards").hasAuthority("VIEWCARDS")
//                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/myAccount").hasRole("USER")//ROLE_USER - in Database
                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        //.requestMatchers("/myLoans").hasRole("USER")
                        .requestMatchers("/myLoans").authenticated()
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices", "/contact", "/register", "/error", "/invalidSession","/apiLogin").permitAll());
        //Default authentication login page where we can enter credentials
        http.formLogin(withDefaults());

        // It will diable form login authentication
        //http.formLogin(flc -> flc.disable());

        //Http basic authentication which will pass credential in basic 64 encoded form like Basic ADSGYUH876
        //http.httpBasic(withDefaults());

        //Using our own exception handler
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthEntryPoint()));

        //Global config for custom auth exception handling
        //http.exceptionHandling(ehc-> ehc.authenticationEntryPoint(new CustomBasicAuthEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        //It will disable http basic authentication
        // http.httpBasic(hbc-> hbc.disable());
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        // If we try to use plain password it won't be able to authorize ad spring by default this password is encoded
//        // by using {noop} we can explicitly mentation to spring that use as plain text
//        UserDetails user = User.withUsername("user").password("{noop}Abbas@143").authorities("read").build();
//        // Hash value of Pathan@143
//        UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$12$3nxsV2eptwnUx5Op9ObgqOXqItVGpQTnqp.DhhCxohWmHIeDmkWJW").authorities("admin").build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        // It will connect to the database which we configured and fetch user details by username
//        // It will force you to use same schema which JdbcUserDetailsManager uses
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is having multiple password encoder, we are using default encoder which is spring boot consider
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //It will disallow user to use common password like password,1234 etc
    //@Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        BankProdUsernameAndPwdAuthenticationProvide authenticationProvider =
                new BankProdUsernameAndPwdAuthenticationProvide(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
