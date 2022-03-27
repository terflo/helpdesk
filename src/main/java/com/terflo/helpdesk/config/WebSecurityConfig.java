package com.terflo.helpdesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
 * Конфигурация безопасности веб-приложения
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

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
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/js/**", "/img/**", "/registration/checkUserData/**").permitAll()
                .antMatchers("/registration", "/activate/**").not().fullyAuthenticated()
                .antMatchers("/requests/free", "/requests/supervised", "/requests/*/accept", "requests/*/close").hasRole("OPERATOR")
                .antMatchers("/admin/**", "/decisions/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/requests/*").hasRole("ADMIN")
                .anyRequest().authenticated()
                    .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                    .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                    .and()
                .sessionManagement()
                .invalidSessionUrl("/login?logout")
                .maximumSessions(1)
                .expiredUrl("/login?logout")
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry());
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
