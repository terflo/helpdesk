package com.terflo.helpdesk.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность решения какой-либо проблемы (частые вопросы)
 */
@Data
@Entity
@Table(name = "decision")
public class Decision {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "preview")
    private String preview;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private Date date;

}
