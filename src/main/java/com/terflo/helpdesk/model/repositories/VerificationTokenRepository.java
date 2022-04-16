package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.entity.enums.VerificationTypeToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByActivateCodeAndUserAndType(String activateCode, User user, VerificationTypeToken type);

    Optional<VerificationToken> findByUserAndType(User user, VerificationTypeToken type);

    List<VerificationToken> findByDateBeforeAndType(Date date, VerificationTypeToken type);

    void deleteByUserAndType(User user, VerificationTypeToken type);
}
