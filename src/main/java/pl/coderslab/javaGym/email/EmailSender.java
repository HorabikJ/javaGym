package pl.coderslab.javaGym.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pl.coderslab.javaGym.entity.Person;
import pl.coderslab.javaGym.entity.email.ActivationEmailDetails;
import pl.coderslab.javaGym.entity.email.ChangeEmailDetails;
import pl.coderslab.javaGym.entity.email.ResetPasswordEmailDetails;
import pl.coderslab.javaGym.entity.user.User;
import pl.coderslab.javaGym.repository.ResetPasswordEmailRepository;
import pl.coderslab.javaGym.service.emailService.ActivationEmailService;
import pl.coderslab.javaGym.service.emailService.ChangeEmailDetailsService;
import pl.coderslab.javaGym.service.emailService.ResetPasswordEmailService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class EmailSender {

    private final static ZoneId ZONE_POLAND = ZoneId.of("Poland");
    private final static Integer LINK_EXPIRATION_TIME = 30;

    private final static String CHANGE_EMAIL_URL = "http://localhost:8080/change-email?param=";
    private final static String CHANGE_EMAIL_SUBJECT = "JavaSpringGym change of email.";
    private final static String CHANGE_EMAIL_TEXT = "Please click below link to confirm change of your email,\n" +
                                        "this link will expire in " + LINK_EXPIRATION_TIME + " minutes.\n";

    private final static String CONFIRM_ACCOUNT_URL = "http://localhost:8080/register/confirm-account?param=";
    private final static String CONFIRM_ACCOUNT_SUBJECT = "JavaGymSpring account activation email.";
    private final static String CONFIRM_ACCOUNT_TEXT = "Please click below link to activate your account,\n" +
                                        "this link will expire in " + LINK_EXPIRATION_TIME + " minutes.\n";

    private final static String RESET_PASSWORD_URL = "http://localhost:8080/reset-password/show-form?param=";
    private final static String RESET_PASSWORD_SUBJECT = "JavaSpringGym reset password.";
    private final static String RESET_PASSWORD_TEXT = "Please click below link to go to reset password site,\n" +
                                        "it will be possible for the next " + LINK_EXPIRATION_TIME + " minutes.\n";

    private final static String WELCOME_EMAIL_SUBJECT = ", welcome in JavaSpringGym application!";
    private final static String WELCOME_EMAIL_TEXT = ", we are very pleased that you joined us!";

    private JavaMailSender javaMailSender;
    private ActivationEmailService activationEmailService;
    private ChangeEmailDetailsService changeEmailDetailsService;
    private ResetPasswordEmailService resetPasswordEmailService;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender,
                       ChangeEmailDetailsService changeEmailDetailsService,
                       ActivationEmailService activationEmailService,
                       ResetPasswordEmailService resetPasswordEmailService) {
        this.changeEmailDetailsService = changeEmailDetailsService;
        this.javaMailSender = javaMailSender;
        this.activationEmailService = activationEmailService;
        this.resetPasswordEmailService = resetPasswordEmailService;
    }

    public void sendUserWelcomeEmail(Person person) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject(person.getFirstName() + WELCOME_EMAIL_SUBJECT);
        message.setText(person.getFirstName() + WELCOME_EMAIL_TEXT);
        javaMailSender.send(message);
    }

    public void sendAccountActivationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        StringBuffer messageText = new StringBuffer();

        String param = UUID.randomUUID().toString();
        messageText.append(CONFIRM_ACCOUNT_TEXT)
                .append(CONFIRM_ACCOUNT_URL)
                .append(param);

        message.setTo(user.getEmail());
        message.setSubject(CONFIRM_ACCOUNT_SUBJECT);
        message.setText(messageText.toString());

        ActivationEmailDetails activationEmailDetails =
                new ActivationEmailDetails
                (user, param, ZonedDateTime.now(ZONE_POLAND), LINK_EXPIRATION_TIME);

        activationEmailService.save(activationEmailDetails);
        javaMailSender.send(message);
    }

    public void sendChangeEmailMessage(User user, String newEmail) {
        StringBuffer messageText = new StringBuffer();
        SimpleMailMessage message = new SimpleMailMessage();

        String param = UUID.randomUUID().toString();
        messageText.append(CHANGE_EMAIL_TEXT)
                .append(CHANGE_EMAIL_URL)
                .append(param);

        message.setTo(newEmail);
        message.setSubject(CHANGE_EMAIL_SUBJECT);
        message.setText(messageText.toString());
        ChangeEmailDetails emailDetails =
                new ChangeEmailDetails(user, param, ZonedDateTime.now(ZONE_POLAND),
                newEmail, LINK_EXPIRATION_TIME);

        changeEmailDetailsService.save(emailDetails);
        javaMailSender.send(message);
    }

    public void sendResetPasswordEmail(User user) {
        StringBuffer messageText = new StringBuffer();
        SimpleMailMessage message = new SimpleMailMessage();

        String param = UUID.randomUUID().toString();
        messageText.append(RESET_PASSWORD_TEXT)
                .append(RESET_PASSWORD_URL)
                .append(param);

        message.setTo(user.getEmail());
        message.setSubject(RESET_PASSWORD_SUBJECT);
        message.setText(messageText.toString());
        ResetPasswordEmailDetails resetPasswordEmailDetails =
                new ResetPasswordEmailDetails
                (user, param, ZonedDateTime.now(ZONE_POLAND), LINK_EXPIRATION_TIME);

        resetPasswordEmailService.save(resetPasswordEmailDetails);
        javaMailSender.send(message);
    }
}
