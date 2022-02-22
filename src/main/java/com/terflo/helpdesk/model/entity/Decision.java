package com.terflo.helpdesk.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Сущность часто задаваемых вопросов и ответов на них (частые вопросы на главной странице)
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "decision")
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
    @Column(name = "answer", nullable = false)
    private String answer;

    /**
     * Дата создания
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Автор ответа
     */
    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
}
