package ai.smartdoc.garage.infra.email.internal.service;

import ai.smartdoc.garage.infra.email.EmailPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class EmailService implements EmailPort {

    @Autowired
    @Qualifier("email")
    private JavaMailSender mailSender;

    @Value("${spring.mail.fromEmail}")
    private String fromEmail;

    @Override
    public String sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromEmail);
        mail.setTo(toEmail);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
        return "success";
    }
}
