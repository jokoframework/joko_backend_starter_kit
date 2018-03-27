package io.github.jokoframework.myproject.security;

import java.util.Map;

public class SecurityResponse {

    private String message;
    
    private Boolean success = false;

    private String refreshToken;

    private long expiration;
    
    private String profile;
    
    private Map<String, Object> custom;
    
    public SecurityResponse() {
	}

	public SecurityResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public long getExpiration() {
		return this.expiration;
	}
	
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Map<String, Object> getCustom() {
		return custom;
	}

	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}
}
