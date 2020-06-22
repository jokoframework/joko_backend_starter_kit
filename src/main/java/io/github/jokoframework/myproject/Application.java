package io.github.jokoframework.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by danicricco on 2/21/18.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.github.jokoframework"})
@EnableJpaRepositories(basePackages = {"io.github.jokoframework"})
@EntityScan(basePackages = {"io.github.jokoframework"})
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
