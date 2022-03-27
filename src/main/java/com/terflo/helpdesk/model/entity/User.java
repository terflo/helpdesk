package com.terflo.helpdesk.model.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Danil Krivoschiokov
 * @version 1.4
 * Сущность пользователя системой (хранится в базе данных)
 */
@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Индификатор пользователя
     */
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Пароль пользователя (преобразуется в хэш)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Почта пользователя
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Дата регистрации
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Описание пользователя
     */
    @Column(name = "description", length = 2048)
    private String description;

    /**
     * Уникальный инидификатор аватара пользователя
     */
    @JoinColumn(name = "avatar_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Image avatar;

    /**
     * Список обращений пользователя
     */
    @JoinColumn(name = "user_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<UserRequest> requests;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (expired != user.expired) return false;
        if (locked != user.locked) return false;
        if (credentials_expired != user.credentials_expired) return false;
        if (enabled != user.enabled) return false;
        if (!id.equals(user.id)) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        if (!email.equals(user.email)) return false;
        if (!date.equals(user.date)) return false;
        if (!Objects.equals(description, user.description)) return false;
        return roles.equals(user.roles);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + roles.hashCode();
        result = 31 * result + (expired ? 1 : 0);
        result = 31 * result + (locked ? 1 : 0);
        result = 31 * result + (credentials_expired ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}
