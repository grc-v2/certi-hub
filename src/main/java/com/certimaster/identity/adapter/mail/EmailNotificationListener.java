package com.certimaster.identity.adapter.mail;

import com.certimaster.identity.domain.event.EmailVerificationRequestedEvent;
import com.certimaster.identity.domain.event.PasswordResetRequestedEvent;
import com.certimaster.identity.domain.event.UserRegisteredEvent;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Outbound mail adapter for the identity module. Listens to identity domain events
 * and delivers the corresponding transactional emails (verification, password reset,
 * welcome) via SMTP.
 *
 * <p>These are intra-module events, so plain Spring transactional event listeners are
 * used ({@code fallbackExecution = true} because some publishers, e.g. password reset,
 * run outside a transaction). Delivery is best-effort: failures are logged and never
 * propagated so that registration / reset flows do not fail when SMTP is down.
 */
@Component
public class EmailNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);

    private final JavaMailSender mailSender;
    private final IdentityMailProperties properties;

    public EmailNotificationListener(JavaMailSender mailSender, IdentityMailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onEmailVerificationRequested(EmailVerificationRequestedEvent event) {
        if (event.getEmail() == null || event.getVerificationToken() == null) {
            return;
        }
        String link = buildLink("/verify-email", event.getVerificationToken());
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 560px; margin: 0 auto; color: #1f2937;">
                  <h2 style="color: #111827;">Xác minh email của bạn</h2>
                  <p>Chào bạn,</p>
                  <p>Cảm ơn bạn đã đăng ký CertiMaster. Vui lòng nhấn vào nút bên dưới để xác minh địa chỉ email:</p>
                  <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; border-radius: 6px; text-decoration: none;">Xác minh email</a>
                  </p>
                  <p>Hoặc mở liên kết sau trong trình duyệt:<br/><a href="%s">%s</a></p>
                  <p style="color: #6b7280; font-size: 13px;">Liên kết sẽ hết hạn sau một thời gian ngắn. Nếu bạn không đăng ký tài khoản, hãy bỏ qua email này.</p>
                </div>
                """.formatted(link, link, link);
        send(event.getEmail(), "Xác minh email của bạn - CertiMaster", html);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPasswordResetRequested(PasswordResetRequestedEvent event) {
        if (event.getEmail() == null || event.getResetToken() == null) {
            return;
        }
        String link = buildLink("/reset-password", event.getResetToken());
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 560px; margin: 0 auto; color: #1f2937;">
                  <h2 style="color: #111827;">Đặt lại mật khẩu</h2>
                  <p>Chào bạn,</p>
                  <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nhấn vào nút bên dưới để tạo mật khẩu mới:</p>
                  <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; border-radius: 6px; text-decoration: none;">Đặt lại mật khẩu</a>
                  </p>
                  <p>Hoặc mở liên kết sau trong trình duyệt:<br/><a href="%s">%s</a></p>
                  <p style="color: #6b7280; font-size: 13px;">Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này. Mật khẩu của bạn sẽ không thay đổi.</p>
                </div>
                """.formatted(link, link, link);
        send(event.getEmail(), "Đặt lại mật khẩu - CertiMaster", html);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegistered(UserRegisteredEvent event) {
        if (event.getEmail() == null) {
            return;
        }
        String displayName = event.getFullName() != null && !event.getFullName().isBlank()
                ? event.getFullName()
                : event.getUsername();
        String loginLink = properties.getFrontendBaseUrl() + "/login";
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 560px; margin: 0 auto; color: #1f2937;">
                  <h2 style="color: #111827;">Chào mừng đến với CertiMaster!</h2>
                  <p>Xin chào %s,</p>
                  <p>Tài khoản của bạn đã được tạo thành công. Hãy bắt đầu luyện thi chứng chỉ, quản lý công việc và khám phá tài nguyên học tập ngay hôm nay.</p>
                  <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 24px; border-radius: 6px; text-decoration: none;">Đăng nhập ngay</a>
                  </p>
                  <p style="color: #6b7280; font-size: 13px;">Chúc bạn học tập hiệu quả!</p>
                </div>
                """.formatted(displayName, loginLink);
        send(event.getEmail(), "Chào mừng đến với CertiMaster!", html);
    }

    private String buildLink(String path, String token) {
        return properties.getFrontendBaseUrl() + path + "?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    private void send(String to, String subject, String html) {
        if (!properties.isEnabled()) {
            log.info("Mail sending disabled (app.mail.enabled=false); skipping email '{}' to {}", subject, to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.debug("Sent email '{}' to {}", subject, to);
        } catch (Exception e) {
            // Best-effort delivery: never let a mail failure break the business flow.
            log.error("Failed to send email '{}' to {}: {}", subject, to, e.getMessage(), e);
        }
    }
}
