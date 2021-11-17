package com.terflo.helpdesk.model.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Класс-сущность ролей пользователей (хранится в базе данных)
 */
@Data
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    /**
     * Индификатор роли
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя роли
     */
    @Column(name = "name")
    private String name;

    /**
     * Множество пользователей, которые имеют данную роль
     */
    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role() {}

    public Role(Long id) {

    }

    @Override
    public String getAuthority() {
        return this.name;
    }
}
