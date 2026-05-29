package com.certimaster.identity.adapter.web.response;

import java.util.List;
import java.util.Set;

public class UserResponse {

    private UserInfo user;
    private Set<String> permissions;

    public UserResponse() {
    }

    public UserResponse(UserInfo user, Set<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserInfo user;
        private Set<String> permissions;

        public Builder user(UserInfo v) { this.user = v; return this; }
        public Builder permissions(Set<String> v) { this.permissions = v; return this; }

        public UserResponse build() {
            UserResponse o = new UserResponse();
            o.user = this.user;
            o.permissions = this.permissions;
            return o;
        }
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private String username;
        private String fullName;
        private String avatarUrl;
        private String phone;
        private String status;
        private Boolean emailVerified;
        private List<RoleInfo> roles;

        public UserInfo() {
        }

        public UserInfo(Long id, String email, String username, String fullName, String avatarUrl,
                        String phone, String status, Boolean emailVerified, List<RoleInfo> roles) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
            this.phone = phone;
            this.status = status;
            this.emailVerified = emailVerified;
            this.roles = roles;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        public List<RoleInfo> getRoles() {
            return roles;
        }

        public void setRoles(List<RoleInfo> roles) {
            this.roles = roles;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long id;
            private String email;
            private String username;
            private String fullName;
            private String avatarUrl;
            private String phone;
            private String status;
            private Boolean emailVerified;
            private List<RoleInfo> roles;

            public Builder id(Long v) { this.id = v; return this; }
            public Builder email(String v) { this.email = v; return this; }
            public Builder username(String v) { this.username = v; return this; }
            public Builder fullName(String v) { this.fullName = v; return this; }
            public Builder avatarUrl(String v) { this.avatarUrl = v; return this; }
            public Builder phone(String v) { this.phone = v; return this; }
            public Builder status(String v) { this.status = v; return this; }
            public Builder emailVerified(Boolean v) { this.emailVerified = v; return this; }
            public Builder roles(List<RoleInfo> v) { this.roles = v; return this; }

            public UserInfo build() {
                UserInfo o = new UserInfo();
                o.id = this.id;
                o.email = this.email;
                o.username = this.username;
                o.fullName = this.fullName;
                o.avatarUrl = this.avatarUrl;
                o.phone = this.phone;
                o.status = this.status;
                o.emailVerified = this.emailVerified;
                o.roles = this.roles;
                return o;
            }
        }
    }

    public static class RoleInfo {
        private Long id;
        private String code;
        private String name;
        private Boolean isPrimary;

        public RoleInfo() {
        }

        public RoleInfo(Long id, String code, String name, Boolean isPrimary) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.isPrimary = isPrimary;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long id;
            private String code;
            private String name;
            private Boolean isPrimary;

            public Builder id(Long v) { this.id = v; return this; }
            public Builder code(String v) { this.code = v; return this; }
            public Builder name(String v) { this.name = v; return this; }
            public Builder isPrimary(Boolean v) { this.isPrimary = v; return this; }

            public RoleInfo build() {
                RoleInfo o = new RoleInfo();
                o.id = this.id;
                o.code = this.code;
                o.name = this.name;
                o.isPrimary = this.isPrimary;
                return o;
            }
        }
    }
}
