package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.entity.enums.VerificationTypeToken;
import com.terflo.helpdesk.model.exceptions.VerificationTokenNotFoundException;

import java.util.List;

public interface VerificationTokenService {

    VerificationToken findByActivateCodeAndUser(String activateCode, User user, VerificationTypeToken type) throws VerificationTokenNotFoundException;

    VerificationToken saveToken(VerificationToken verificationToken);

    List<VerificationToken> findAllOldTokens(VerificationTypeToken type);

    void deleteByID(Long id) throws VerificationTokenNotFoundException;

    void deleteByUser(User user, VerificationTypeToken type) throws VerificationTokenNotFoundException;

    void deleteToken(VerificationToken verificationToken) throws VerificationTokenNotFoundException;

    void deleteToken(List<VerificationToken> verificationTokens) throws VerificationTokenNotFoundException;

}
