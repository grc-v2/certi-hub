package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$",
            message = "Username must be 3-50 characters and contain only letters, numbers, underscore, and hyphen")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean acceptTerms;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String username, String password, String fullName, String phone, Boolean acceptTerms) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.acceptTerms = acceptTerms;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String username;
        private String password;
        private String fullName;
        private String phone;
        private Boolean acceptTerms;

        public Builder email(String v) { this.email = v; return this; }
        public Builder username(String v) { this.username = v; return this; }
        public Builder password(String v) { this.password = v; return this; }
        public Builder fullName(String v) { this.fullName = v; return this; }
        public Builder phone(String v) { this.phone = v; return this; }
        public Builder acceptTerms(Boolean v) { this.acceptTerms = v; return this; }

        public RegisterRequest build() {
            RegisterRequest o = new RegisterRequest();
            o.email = this.email;
            o.username = this.username;
            o.password = this.password;
            o.fullName = this.fullName;
            o.phone = this.phone;
            o.acceptTerms = this.acceptTerms;
            return o;
        }
    }
}
