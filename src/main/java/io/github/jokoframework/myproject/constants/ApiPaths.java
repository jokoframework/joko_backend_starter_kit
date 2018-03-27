package io.github.jokoframework.myproject.constants;

/**
 * 
 * @author bsandoval
 *
 */
public class ApiPaths {
    public static final String API_PATTERN = "/api/.*";
    public static final String BASE = "/api";
    
    public static final String ROOT_DIAGNOSTIC = "/diagnostic";
    public static final String SUFFIX_HEART_BEAT = "/heartbeat";
    
    public static final String API_SECURE = "/api/secure";

    /**
     * routes for joko
     */
    public static final String API_SESSIONS = BASE + "/sessions";

    /**
     * routes for user's management
     */
    public static final String ROOT_USERS = API_SECURE + "/users";
    
    public static final String USERS_HEARTBEAT = ROOT_USERS + SUFFIX_HEART_BEAT;
    
    public static final String USERS_BY_NAME = ROOT_USERS + "/{username}";
    
    public static final String USERS_CSV = ROOT_USERS + "/csv";

    /**
     * routes for countries management
     */
    public static final String COUNTRIES = BASE + "/countries";

    /**
     * routes for people management
     */
    public static final String ROOT_PERSON = API_SECURE + "/person";

    public static final String PERSON_HEARTBEAT = ROOT_PERSON + SUFFIX_HEART_BEAT;

    public static final String PERSON_BY_NAME = ROOT_PERSON + "/{name}";

    public static final String PERSON_CSV = ROOT_PERSON + "/csv";
    
    private ApiPaths() {
        
    }

}
