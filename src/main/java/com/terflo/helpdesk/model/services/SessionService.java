package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("expireUserService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionService {

    private SessionRegistry sessionRegistry;

    public void expireUserSessions(String username) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                User user = (User) principal;
                if (user.getUsername().equals(username)) {
                    System.out.println("Пользователь " + user.getUsername() + " выгнан из сессии");
                    for (SessionInformation information : sessionRegistry.getAllSessions(user, true)) {
                        information.expireNow();
                    }
                }
            }
        }
    }

    public void expireUserSessions(Long id) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                User user = (User) principal;
                if (user.getId().equals(id)) {
                    System.out.println("Пользователь " + user.getUsername() + " выгнан из сессии");
                    for (SessionInformation information : sessionRegistry.getAllSessions(user, true)) {
                        information.expireNow();
                    }
                }
            }
        }
    }
}
