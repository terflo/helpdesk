package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.exceptions.VerificationTokenNotFoundException;
import com.terflo.helpdesk.model.repositories.VerificationTokenRepository;
import com.terflo.helpdesk.model.services.interfaces.VerificationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    //TODO: Получать кол-во дней просроченности аккаунта в конфиге приложения
    private final static int DAYS_BEFORE_DELETE_NOT_ACTIVATED_USER = 7;

    private final VerificationTokenRepository verificationTokenRepository;


    @Override
    public VerificationToken findByActivateCode(String activateCode) throws VerificationTokenNotFoundException {
        return verificationTokenRepository
                .findByActivateCode(activateCode)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Токен верификации " + activateCode + " не найден"));
    }


    @Override
    public List<VerificationToken> findAllOldTokens() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_WEEK, -DAYS_BEFORE_DELETE_NOT_ACTIVATED_USER);
        return verificationTokenRepository.findByDateBefore(currentDate.getTime());
    }


    @Override
    @Transactional
    public void deleteByID(Long id) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findById(id).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации с инидификатором " + id +" не найден");
        else
            verificationTokenRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void deleteByUser(User user) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findByUser(user).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации пользователя " + user.getUsername() + " не найден");
        else
            verificationTokenRepository.deleteByUser(user);
    }


    @Override
    @Transactional
    public void deleteToken(VerificationToken verificationToken) throws VerificationTokenNotFoundException {
        if(!verificationTokenRepository.findById(verificationToken.getId()).isPresent())
            throw new VerificationTokenNotFoundException("Токен верификации " + verificationToken.getActivateCode() + " не найден");
        else
            verificationTokenRepository.delete(verificationToken);
    }


    @Override
    @Transactional
    public void deleteToken(List<VerificationToken> verificationTokens) throws VerificationTokenNotFoundException {
        for(VerificationToken token : verificationTokens) {
            this.deleteToken(token);
        }
    }


    @Override
    public VerificationToken saveToken(VerificationToken verificationToken) {
        return verificationTokenRepository.save(verificationToken);
    }

}
