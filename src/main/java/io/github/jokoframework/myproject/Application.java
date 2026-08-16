package io.github.jokoframework.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = "io.github.jokoframework.myproject",
        exclude = UserDetailsServiceAutoConfiguration.class)
@EnableJpaRepositories(basePackages = "io.github.jokoframework.myproject")
@EntityScan(basePackages = "io.github.jokoframework.myproject")
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
