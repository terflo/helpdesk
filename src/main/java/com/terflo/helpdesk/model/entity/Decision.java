package com.terflo.helpdesk.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Сущность часто задаваемых вопросов и ответов на них (частые вопросы на главной странице)
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "decisions")
public class Decision {

    /**
     * Уникальный индификатор сущности
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя часто задаваемого вопроса
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /**
     * Ответ на часто задаваемый вопрос
     */
    @Column(name = "answer", nullable = false, length = 2048)
    private String answer;

    /**
     * Дата создания
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Автор ответа
     */
    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Decision decision = (Decision) o;
        return id != null && Objects.equals(id, decision.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
