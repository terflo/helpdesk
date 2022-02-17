package com.terflo.helpdesk.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность пользователя системой (хранится в базе данных)
 */
@Data
@Entity
@Transactional
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Индификатор пользователя
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Пароль пользователя (преобразуется в хэш)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Почта пользователя
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Дата регистрации
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Множество ролей пользователя
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    /**
     * Просроченность аккаунта
     */
    @Column(name = "expired")
    private boolean expired;

    /**
     * Блокировка аккаунта
     */
    @Column(name = "locked")
    private boolean locked;

    /**
     * Просроченность данных аккаунта
     */
    @Column(name = "credentials_expired")
    private boolean credentials_expired;

    /**
     * Статус выключен/включен аккаунт
     */
    @Column(name = "enabled")
    private boolean enabled;

    public void switchLock() {
        this.locked = !this.locked;
    }

    public Set<String> getRoleNames() {
        return this.roles
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentials_expired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
