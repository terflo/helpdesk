package com.terflo.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Конфигурация безопасности веб-приложения
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Функция создания бина с кодировщиком паролей
     * @return кодировщик паролей
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Функция конфигурирования
     * @param http объект конфигурация
     * @throws Exception возникает в результате ошибки :)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/registration", "/js/**", "/css/**", "/img/**").permitAll()
                .anyRequest().authenticated()
                    .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                    .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/index.html")
                .permitAll();
    }
}
