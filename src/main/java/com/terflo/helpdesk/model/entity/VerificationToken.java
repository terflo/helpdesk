package com.terflo.helpdesk.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;

    @Lob
    @Column(name = "activate_code", nullable = false)
    private String activateCode;

    @Column(name = "date", nullable = false)
    private Date date;

    public static VerificationToken generateToken(User user) {
        return new VerificationToken(
                null,
                user,
                UUID.randomUUID().toString(),
                new Date()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VerificationToken that = (VerificationToken) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + activateCode.hashCode();
        return result;
    }
}
