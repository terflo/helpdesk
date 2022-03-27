package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByActivateCode(String activateCode);

    Optional<VerificationToken> findByUser(User user);

    List<VerificationToken> findByDateBefore(Date date);

    void deleteByUser(User user);
}
