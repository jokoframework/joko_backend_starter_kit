package io.github.jokoframework.myproject.web.session;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class UserCurrentInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String username;
    private boolean authenticated;
    private String name;
    private String lastName;
    private String email;
    private String birthDate;
    private String profile;

    @JsonIgnore
    private String refreshToken;

    public UserCurrentInfo() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class Builder {
        private String username;
        private boolean authenticated;
        private String name;
        private String lastName;
        private String email;
        private String birthDate;
        private String profile;
        private String refreshToken;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder birthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public UserCurrentInfo build() {
            return new UserCurrentInfo(this);
        }

    }

    private UserCurrentInfo(Builder builder) {
        this.username = builder.username;
        this.authenticated = builder.authenticated;
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.birthDate = builder.birthDate;
        this.profile = builder.profile;
        this.refreshToken = builder.refreshToken;
    }

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("UserCurrentInfo [username=").append(username).append(", authenticated=").append(authenticated)
                .append(", name=").append(name).append(", lastName=").append(lastName).append(", email=").append(email)
                .append(", birthDate=").append(birthDate).append(", profile=").append(profile).append(", refreshToken=")
                .append(refreshToken).append("]");
        return builder2.toString();
    }

}
