package com.terflo.helpdesk.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Base64;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "bytes", nullable = false)
    private byte[] bytes;

    @Column(name = "type", nullable = false)
    private String type;

    public String getBase64ImageWithType() {
        return "data:" + this.type + ";base64," + Base64.getEncoder().encodeToString(this.bytes);
    }
}
