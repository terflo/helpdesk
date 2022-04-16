package com.terflo.helpdesk.model.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Log4j2
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine springTemplateEngine;

    @Value("${domain.name}")
    private String SERVER_ADDRESS;

    @Value("${application.name}")
    private String FROM;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Async
    public void sendRegistrationMail(String address, String username, String activateCode) throws MessagingException {
        log.info("Письмо с подтверждением регистрации на адрес " + address + " принято в обработку");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("activateCode", activateCode);
        context.setVariable("URL", String.format("http://%s/activate/%s/%s",
                SERVER_ADDRESS,
                username,
                activateCode));

        String emailContent = springTemplateEngine.process("mail/user-activate", context);

        mimeMessageHelper.setTo(address);
        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setSubject("Активация аккаунта");
        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(message);

        log.info("Письмо с подтверждением регистрации на адрес " + address + " отправлено");
    }

    @Async
    public void sendConfirmEmailAddress(String username, String address, String activateCode) throws MessagingException {
        log.info("Письмо с подтверждением нового почтового ящика на адрес " + address + " принято в обработку");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("activateCode", activateCode);
        context.setVariable("URL", String.format("http://%s/activate/%s/%s",
                SERVER_ADDRESS,
                username,
                activateCode));

        String emailContent = springTemplateEngine.process("mail/email-activate", context);

        mimeMessageHelper.setTo(address);
        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setSubject("Подтверждение почты");
        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(message);

        log.info("Письмо с подтверждением нового почтового ящика на адрес " + address + " отправлено");
    }

    @Async
    public void sendChangePasswordEmail(String username, String address, String activateCode) throws MessagingException {
        log.info("Письмо с подтверждением изменения пароля на адрес " + address + " принято в обработку");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("activateCode", activateCode);
        context.setVariable("URL", String.format("http://%s/users/changePassword?username=%s&activate_code=%s",
                SERVER_ADDRESS,
                username,
                activateCode));

        String emailContent = springTemplateEngine.process("mail/password-change", context);

        mimeMessageHelper.setTo(address);
        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setSubject("Изменение пароля");
        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(message);

        log.info("Письмо с подтверждением изменения пароля на адрес " + address + " отправлено");
    }
}
