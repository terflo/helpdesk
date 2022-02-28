package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.exceptions.VerificationTokenNotFoundException;
import com.terflo.helpdesk.model.repositories.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken findByActivateCode(String activateCode) throws VerificationTokenNotFoundException {
        return verificationTokenRepository
                .findByActivateCode(activateCode)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Токен верификации " + activateCode + " не найден"));
    }

    @Transactional
    public void deleteByID(Long id) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findById(id).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации с инидификатором " + id +" не найден");
        else
            verificationTokenRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUser(User user) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findByUser(user).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации пользователя " + user.getUsername() + " не найден");
        else
            verificationTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteToken(VerificationToken verificationToken) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findById(verificationToken.getId()).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации " + verificationToken.getActivateCode() + " не найден");
        else
            verificationTokenRepository.delete(verificationToken);
    }

    public void saveToken(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

}
