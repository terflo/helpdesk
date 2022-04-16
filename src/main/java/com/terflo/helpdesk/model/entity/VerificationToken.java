package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.VerificationTypeToken;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Токен верификации регистрации пользователя (создается при регистрации нового пользователя)
 * @version 1.1
 * @author Danil Krivoschiokov
 */
@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_tokens")
public class VerificationToken {

    /**
     * Уникальный индификатор токена
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, для которого предназначен токен
     */
    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;

    /**
     * Код активации
     */
    @Lob
    @Column(name = "activate_code", nullable = false)
    private String activateCode;

    /**
     * Дата создания токена
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Тип токена
     */
    @Column(name = "token_type", nullable = false)
    private VerificationTypeToken type;


    /**
     * Статический метод генерации нового токена подтверждения регисатрции
     * @param user пользователь, для которого предназначен токен
     * @param type тип токена
     * @return новый токен
     */
    public static VerificationToken generateToken(User user, VerificationTypeToken type) {
        return new VerificationToken(
                null,
                user,
                UUID.randomUUID().toString(),
                new Date(),
                type
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VerificationToken that = (VerificationToken) o;

        if (!id.equals(that.id)) return false;
        if (!activateCode.equals(that.activateCode)) return false;
        if (!date.equals(that.date)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + activateCode.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
