package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Email(message = "Email must be valid")
    @Size(max = 300, message = "Email must not exceed 300 characters")
    private String email;

    @Size(max = 300, message = "Full name must not exceed 300 characters")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be valid")
    private String phone;

    private String roleCode;

    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED|LOCKED", 
             message = "Status must be ACTIVE, INACTIVE, SUSPENDED, or LOCKED")
    private String status;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String email, String fullName, String phone, String roleCode, String status) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.roleCode = roleCode;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
