package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.config.CaptchaSettings;
import com.terflo.helpdesk.model.exceptions.InvalidReCaptchaException;
import com.terflo.helpdesk.model.responses.GoogleResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CaptchaService {

    private final ReCaptchaAttemptService reCaptchaAttemptService;

    private final CaptchaSettings captchaSettings;

    private final RestOperations restTemplate;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private static final String VERIFY_URI = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    public void processResponse(String response, String clientIP) throws InvalidReCaptchaException {
        if(!responseSanityCheck(response)) {
            throw new InvalidReCaptchaException("Ошибка проверки капчи");
        }

        if(reCaptchaAttemptService.isBlocked(clientIP)) {
            throw new InvalidReCaptchaException("Клиент превысил максимальное количество неудачных попыток");
        }

        URI verifyUri = URI.create(String.format(
                VERIFY_URI,
                captchaSettings.getSecret(), response, clientIP));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(!Objects.requireNonNull(googleResponse).isSuccess()) {
            if(googleResponse.hasClientError()) {
                reCaptchaAttemptService.reCaptchaFailed(clientIP);
            }
                throw new InvalidReCaptchaException("reCAPTCHA не была успешно проверена");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(clientIP);

        if(!Objects.requireNonNull(googleResponse).isSuccess()) {
            throw new InvalidReCaptchaException("reCAPTCHA не была успешно проверена");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}
