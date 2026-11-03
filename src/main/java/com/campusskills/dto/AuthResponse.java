package com.campusskills.dto;

public class AuthResponse {
    private String token;
    private UserResponse user;

    public AuthResponse() {}

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return this.user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private UserResponse user;

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this.token, this.user);
        }
    }

}
