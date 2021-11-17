package com.terflo.helpdesk.model.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность пользователя системой (хранится в базе данных)
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Индификатор пользователя
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Имя пользователя
     */
    @Column(name = "username")
    String username;

    /**
     * Пароль пользователя (преобразуется в хэш)
     */
    @Column(name = "password")
    String password;

    /**
     * Почта пользователя
     */
    @Column(name = "email")
    String email;

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
