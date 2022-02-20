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
    @Column(name = "bytes")
    private byte[] bytes;

    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }
}