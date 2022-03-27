package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.exceptions.VerificationTokenNotFoundException;

import java.util.List;

public interface VerificationTokenService {

    VerificationToken findByActivateCode(String activateCode) throws VerificationTokenNotFoundException;

    VerificationToken saveToken(VerificationToken verificationToken);

    List<VerificationToken> findAllOldTokens();

    void deleteByID(Long id) throws VerificationTokenNotFoundException;

    void deleteByUser(User user) throws VerificationTokenNotFoundException;

    void deleteToken(VerificationToken verificationToken) throws VerificationTokenNotFoundException;

    void deleteToken(List<VerificationToken> verificationTokens) throws VerificationTokenNotFoundException;

}
