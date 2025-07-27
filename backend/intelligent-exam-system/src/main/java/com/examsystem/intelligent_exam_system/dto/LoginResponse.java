package com.examsystem.intelligent_exam_system.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserInfo user;

    // 构造函数
    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, String token, UserInfo user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    // Getter和Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    // 内部类：用户信息
    public static class UserInfo {
        private Long userId;
        private String username;
        private String role;
        private String fullName;
        private Boolean isFirstLogin;

        public UserInfo() {
        }

        public UserInfo(Long userId, String username, String role, String fullName, Boolean isFirstLogin) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
            this.isFirstLogin = isFirstLogin;
        }

        // Getter和Setter
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Boolean getIsFirstLogin() {
            return isFirstLogin;
        }

        public void setIsFirstLogin(Boolean isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }
    }
}