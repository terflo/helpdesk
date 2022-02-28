package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByActivateCode(String activateCode);

    Optional<VerificationToken> findByUser(User user);

    void deleteByUser(User user);
}
