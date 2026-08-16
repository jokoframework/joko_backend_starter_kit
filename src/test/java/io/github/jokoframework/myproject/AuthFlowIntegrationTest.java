package io.github.jokoframework.myproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTest {

    private static final String AUTH_HEADER = "X-JOKO-AUTH";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void countriesArePublic() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/countries", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Paraguay");
    }

    @Test
    void usersWithoutTokenAreRejected() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/secure/users/admin", String.class);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void loginAccessTokenAndProtectedUser() {
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> login = restTemplate.postForEntity(
                "/api/login",
                new HttpEntity<>("{\"username\":\"admin\",\"password\":\"123456\"}", loginHeaders),
                Map.class);
        assertThat(login.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(login.getBody()).isNotNull();
        String refreshToken = (String) login.getBody().get("secret");
        assertThat(refreshToken).isNotBlank();

        HttpHeaders refreshHeaders = new HttpHeaders();
        refreshHeaders.set(AUTH_HEADER, refreshToken);
        ResponseEntity<Map> access = restTemplate.exchange(
                "/api/token/user-access",
                HttpMethod.POST,
                new HttpEntity<>(refreshHeaders),
                Map.class);
        assertThat(access.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(access.getBody()).isNotNull();
        String accessToken = (String) access.getBody().get("secret");
        assertThat(accessToken).isNotBlank();

        HttpHeaders apiHeaders = new HttpHeaders();
        apiHeaders.set(AUTH_HEADER, accessToken);
        ResponseEntity<String> user = restTemplate.exchange(
                "/api/secure/users/admin",
                HttpMethod.GET,
                new HttpEntity<>(apiHeaders),
                String.class);
        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user.getBody()).contains("admin");
    }
}
