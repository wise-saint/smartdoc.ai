package ai.smartdoc.garage.infra.email;

public interface EmailPort {

    public String sendEmail(String toEmail, String subject, String body);
}
