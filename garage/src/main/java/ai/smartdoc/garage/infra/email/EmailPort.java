package ai.smartdoc.garage.infra.email;

public interface EmailPort {

    String sendEmail(String toEmail, String subject, String body);
}
