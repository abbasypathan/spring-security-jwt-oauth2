package com.springsecurity.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationEvents {

    // By default, spring boot does not produce authorization success events we can configure if required
    @EventListener
    public void onFailure(AuthorizationDeniedEvent authorizationDeniedEvent) {
        log.error("Authorization failed for the user : {} due to : {}", authorizationDeniedEvent.getAuthentication().get().getName(),
                authorizationDeniedEvent.getAuthorizationResult());
    }
}
