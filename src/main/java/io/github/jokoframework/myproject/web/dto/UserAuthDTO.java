package io.github.jokoframework.myproject.web.dto;

import io.github.jokoframework.myproject.profile.dto.UserDTO;

/**
 * Object for user authentication purpose
 * 
 * @author bsandoval
 *
 */
public class UserAuthDTO extends UserDTO {
	private String password;
	private String profile;

    public UserAuthDTO() {
        super();
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAuthDTO [password=").append(password).append(", profile=").append(profile)
				.append(", getId()=").append(getId()).append(", getUsername()=").append(getUsername())
				.append(", getName()=").append(getName()).append(", getLastName()=").append(getLastName())
				.append(", getLastAccessDate()=").append(getLastAccessDate())
				.append("]");
		return builder.toString();
	}
    
}
