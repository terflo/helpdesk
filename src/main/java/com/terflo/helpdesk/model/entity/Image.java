package com.terflo.helpdesk.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Base64;

/**
 * Сущность изображения
 * @author Danil Krivoschiokov
 * @version 1.2
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {

    /**
     * Уникальный индификатор изображения
     */
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Байт-код изображения
     */
    @Lob
    @Column(name = "bytes", nullable = false)
    private byte[] bytes;

    /**
     * Тип изображения
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Метод получения base64 хэш из байт-кода изображения
     * @return строка base64
     */
    public String getBase64ImageWithType() {
        return "data:" + this.type + ";base64," + Base64.getEncoder().encodeToString(this.bytes);
    }
}
