package io.github.jokoframework.myproject.security;

import com.google.gson.Gson;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationDetails extends WebAuthenticationDetails implements AuthenticationDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, Object> custom = new HashMap<>();
	
	public static final String USER_DATE = "userDate";
	  
	public static final String IP_ADDRESS = "ipAddress";
	
	public static final String HOST = "host";
	
	public static final String USER_AGENT = "userAgent";
	
	public CustomAuthenticationDetails(HttpServletRequest request) {
		super(request);
		try {
			this.custom = (HashMap<String, Object>) customFromRequest(request);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
	public CustomAuthenticationDetails addCustom(String key, Object value) {
		this.custom.put(key, value);
		return this;
	}

	private Map<String, Object> customFromRequest(HttpServletRequest request) throws IOException {
		// must be automatic
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		AuthenticationDetail details = gson.fromJson(reader, CustomAuthenticationDetails.class);
		Map<String, Object> builtCustom = details.getCustom();
		parseTypes(builtCustom);
		return builtCustom;
	}

	private void parseTypes(Map<String, Object> builtCustom) {		
		Object userDate = builtCustom.get(USER_DATE);
		if (userDate instanceof Long) {
			Long l = Long.parseLong(userDate.toString());
			builtCustom.put(USER_DATE, l);
		}
	}
	
	@Override
	public Map<String, Object> getCustom() {
		return this.custom;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Custom: ").append(this.getCustom());

		return sb.toString();

	}
	
}