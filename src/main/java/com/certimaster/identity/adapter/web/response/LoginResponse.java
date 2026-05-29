package com.certimaster.identity.adapter.web.response;

import java.util.List;
import java.util.Set;

public class LoginResponse {

    private UserInfo user;
    private TokenInfo tokens;
    private Set<String> permissions;

    public LoginResponse() {
    }

    public LoginResponse(UserInfo user, TokenInfo tokens, Set<String> permissions) {
        this.user = user;
        this.tokens = tokens;
        this.permissions = permissions;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public TokenInfo getTokens() {
        return tokens;
    }

    public void setTokens(TokenInfo tokens) {
        this.tokens = tokens;
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
        private TokenInfo tokens;
        private Set<String> permissions;

        public Builder user(UserInfo v) { this.user = v; return this; }
        public Builder tokens(TokenInfo v) { this.tokens = v; return this; }
        public Builder permissions(Set<String> v) { this.permissions = v; return this; }

        public LoginResponse build() {
            LoginResponse o = new LoginResponse();
            o.user = this.user;
            o.tokens = this.tokens;
            o.permissions = this.permissions;
            return o;
        }
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private String username;
        private String fullName;
        private String avatar;
        private List<RoleInfo> roles;

        public UserInfo() {
        }

        public UserInfo(Long id, String email, String username, String fullName, String avatar, List<RoleInfo> roles) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.fullName = fullName;
            this.avatar = avatar;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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
            private String avatar;
            private List<RoleInfo> roles;

            public Builder id(Long v) { this.id = v; return this; }
            public Builder email(String v) { this.email = v; return this; }
            public Builder username(String v) { this.username = v; return this; }
            public Builder fullName(String v) { this.fullName = v; return this; }
            public Builder avatar(String v) { this.avatar = v; return this; }
            public Builder roles(List<RoleInfo> v) { this.roles = v; return this; }

            public UserInfo build() {
                UserInfo o = new UserInfo();
                o.id = this.id;
                o.email = this.email;
                o.username = this.username;
                o.fullName = this.fullName;
                o.avatar = this.avatar;
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

    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;

        public TokenInfo() {
        }

        public TokenInfo(String accessToken, String refreshToken, String tokenType, Long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String accessToken;
            private String refreshToken;
            private String tokenType;
            private Long expiresIn;

            public Builder accessToken(String v) { this.accessToken = v; return this; }
            public Builder refreshToken(String v) { this.refreshToken = v; return this; }
            public Builder tokenType(String v) { this.tokenType = v; return this; }
            public Builder expiresIn(Long v) { this.expiresIn = v; return this; }

            public TokenInfo build() {
                TokenInfo o = new TokenInfo();
                o.accessToken = this.accessToken;
                o.refreshToken = this.refreshToken;
                o.tokenType = this.tokenType;
                o.expiresIn = this.expiresIn;
                return o;
            }
        }
    }
}
