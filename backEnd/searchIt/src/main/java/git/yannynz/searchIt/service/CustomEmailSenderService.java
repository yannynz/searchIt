package git.yannynz.searchIt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@Service
public class CustomEmailSenderService implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String defaultFrom;

    public CustomEmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

   @Async
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(defaultFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            log.info("Enviando e-mail para {} com o assunto: {}", to, subject);
            mailSender.send(message);
            log.info("E-mail enviado com sucesso para {}", to);
        } catch (MailException e) {
            log.error("Erro ao enviar e-mail para {}: {}", to, e.getMessage());
        }
    }
}

