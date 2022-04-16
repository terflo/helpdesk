package com.terflo.helpdesk.config;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.entity.enums.VerificationTypeToken;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.VerificationTokenNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import com.terflo.helpdesk.model.services.interfaces.VerificationTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final VerificationTokenService verificationTokenService;

    private final UserService userService;


    public SchedulingConfig(@Lazy VerificationTokenService verificationTokenService, @Lazy UserService userService) {
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
    }


    @Scheduled(timeUnit = TimeUnit.DAYS, fixedDelay = 7L)
    public void deleteNotActivatedUsers() {
        log.info("Начат процесс удаления неактивированных пользователей...");
        List<VerificationToken> tokens = verificationTokenService.findAllOldTokens(VerificationTypeToken.REGISTRATION_CONFIRM);
        if(!tokens.isEmpty()) {
            log.info(String.format("Пользователей к удалению: %s", tokens.size()));
            List<User> users = tokens.stream().map(VerificationToken::getUser).collect(Collectors.toList());
            try {
                verificationTokenService.deleteToken(tokens);
                userService.deleteUser(users);
            } catch (UserNotFoundException | VerificationTokenNotFoundException e) {
                log.error("Возникло исключение во время удаления неактивированного пользователя:\n" + e.getMessage());
            }
            log.info("Процесс удаления неактивированных пользователей завершен");
        } else {
            log.info("Пользователей к удалению не найдено");
        }
    }


    @Scheduled(timeUnit = TimeUnit.DAYS, fixedDelay = 1L)
    public void deleteNotActivatedChangePasswordTokens() {
        log.info("Начат процесс удаления неактивированных токенов смены пароля...");
        List<VerificationToken> tokens = verificationTokenService.findAllOldTokens(VerificationTypeToken.CHANGE_PASSWORD);
        if(!tokens.isEmpty()) {
            log.info(String.format("Токенов изменения пароля к удалению: %s", tokens.size()));
            try {
                verificationTokenService.deleteToken(tokens);
            } catch (VerificationTokenNotFoundException e) {
                log.error("Возникло исключение во время удаления неактивированного токена изменения пароля:\n" + e.getMessage());
            }
            log.info("Процесс удаления токенов изменения пароля завершен");
        } else {
            log.info("Токенов к удалению не найдено");
        }
    }

}
