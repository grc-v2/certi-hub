package com.certimaster.identity.adapter.mail;

import com.certimaster.identity.domain.event.EmailVerificationRequestedEvent;
import com.certimaster.identity.domain.event.PasswordResetRequestedEvent;
import com.certimaster.identity.domain.event.UserRegisteredEvent;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EmailNotificationListenerTest {

    @Mock
    private JavaMailSender mailSender;

    private IdentityMailProperties properties;
    private EmailNotificationListener listener;

    @BeforeEach
    void setUp() {
        properties = new IdentityMailProperties();
        properties.setEnabled(true);
        properties.setFrom("no-reply@certimaster.com");
        properties.setFrontendBaseUrl("http://localhost:3000");
        listener = new EmailNotificationListener(mailSender, properties);

        lenient().when(mailSender.createMimeMessage())
                .thenAnswer(invocation -> new MimeMessage((jakarta.mail.Session) null));
    }

    @Test
    void verificationEmailIsSentToUserWithVerifyLink() throws Exception {
        EmailVerificationRequestedEvent event = new EmailVerificationRequestedEvent(
                42L, "learner@example.com", "verify-token-123",
                LocalDateTime.now().plusHours(24), LocalDateTime.now());

        listener.onEmailVerificationRequested(event);

        MimeMessage message = captureSentMessage();
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .containsExactly("learner@example.com");
        assertThat(message.getFrom()).extracting(Object::toString)
                .containsExactly("no-reply@certimaster.com");
        assertThat(message.getSubject()).isEqualTo("Xác minh email của bạn - CertiMaster");
        assertThat(content(message)).contains("http://localhost:3000/verify-email?token=verify-token-123");
    }

    @Test
    void passwordResetEmailIsSentToUserWithResetLink() throws Exception {
        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent(
                42L, "learner@example.com", "reset-token-456",
                LocalDateTime.now().plusMinutes(30), LocalDateTime.now());

        listener.onPasswordResetRequested(event);

        MimeMessage message = captureSentMessage();
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .containsExactly("learner@example.com");
        assertThat(message.getSubject()).isEqualTo("Đặt lại mật khẩu - CertiMaster");
        assertThat(content(message)).contains("http://localhost:3000/reset-password?token=reset-token-456");
    }

    @Test
    void welcomeEmailIsSentOnRegistrationUsingFullName() throws Exception {
        UserRegisteredEvent event = new UserRegisteredEvent(
                42L, "learner@example.com", "learner", "Learner User", LocalDateTime.now());

        listener.onUserRegistered(event);

        MimeMessage message = captureSentMessage();
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .containsExactly("learner@example.com");
        assertThat(message.getSubject()).isEqualTo("Chào mừng đến với CertiMaster!");
        assertThat(content(message))
                .contains("Learner User")
                .contains("http://localhost:3000/login");
    }

    @Test
    void welcomeEmailFallsBackToUsernameWhenFullNameIsBlank() throws Exception {
        UserRegisteredEvent event = new UserRegisteredEvent(
                42L, "learner@example.com", "learner", "  ", LocalDateTime.now());

        listener.onUserRegistered(event);

        assertThat(content(captureSentMessage())).contains("learner");
    }

    @Test
    void verificationLinkTokenIsUrlEncoded() throws Exception {
        EmailVerificationRequestedEvent event = new EmailVerificationRequestedEvent(
                42L, "learner@example.com", "a token+special",
                LocalDateTime.now().plusHours(24), LocalDateTime.now());

        listener.onEmailVerificationRequested(event);

        assertThat(content(captureSentMessage()))
                .contains("/verify-email?token=a+token%2Bspecial");
    }

    @Test
    void noEmailIsSentWhenMailIsDisabled() {
        properties.setEnabled(false);

        listener.onUserRegistered(new UserRegisteredEvent(
                42L, "learner@example.com", "learner", "Learner User", LocalDateTime.now()));

        verifyNoInteractions(mailSender);
    }

    @Test
    void eventsWithoutEmailOrTokenAreIgnored() {
        listener.onUserRegistered(new UserRegisteredEvent(
                42L, null, "learner", "Learner User", LocalDateTime.now()));
        listener.onEmailVerificationRequested(new EmailVerificationRequestedEvent(
                42L, "learner@example.com", null, LocalDateTime.now(), LocalDateTime.now()));
        listener.onPasswordResetRequested(new PasswordResetRequestedEvent(
                42L, null, "reset-token", LocalDateTime.now(), LocalDateTime.now()));

        verifyNoInteractions(mailSender);
    }

    @Test
    void sendFailuresAreSwallowedAndNeverPropagated() {
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(MimeMessage.class));

        assertThatCode(() -> listener.onUserRegistered(new UserRegisteredEvent(
                42L, "learner@example.com", "learner", "Learner User", LocalDateTime.now())))
                .doesNotThrowAnyException();
        assertThatCode(() -> listener.onEmailVerificationRequested(new EmailVerificationRequestedEvent(
                42L, "learner@example.com", "verify-token", LocalDateTime.now(), LocalDateTime.now())))
                .doesNotThrowAnyException();
        assertThatCode(() -> listener.onPasswordResetRequested(new PasswordResetRequestedEvent(
                42L, "learner@example.com", "reset-token", LocalDateTime.now(), LocalDateTime.now())))
                .doesNotThrowAnyException();
    }

    @Test
    void createMimeMessageFailuresAreSwallowed() {
        doThrow(new IllegalStateException("no session")).when(mailSender).createMimeMessage();

        assertThatCode(() -> listener.onUserRegistered(new UserRegisteredEvent(
                42L, "learner@example.com", "learner", "Learner User", LocalDateTime.now())))
                .doesNotThrowAnyException();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    private MimeMessage captureSentMessage() {
        var captor = org.mockito.ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        return captor.getValue();
    }

    private String content(MimeMessage message) throws Exception {
        return message.getContent().toString();
    }
}
