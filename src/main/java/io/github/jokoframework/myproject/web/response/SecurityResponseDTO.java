package io.github.jokoframework.myproject.web.response;

import java.util.Map;

/**
 * 
 * @author bsandoval
 *
 */
public class SecurityResponseDTO extends BaseResponseDTO{
    
    private String refreshToken;
    private long expiration;
    private String profile;
    private Map<String, Object> custom;
    
    public SecurityResponseDTO() {
        super();
	}

	public SecurityResponseDTO(boolean success, String message) {
		super(success, message);
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
