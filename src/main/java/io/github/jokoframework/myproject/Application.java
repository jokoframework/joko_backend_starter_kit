package io.github.jokoframework.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by danicricco on 2/21/18.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.github.jokoframework.security","io.github.jokoframework.myproject"})
@EnableJpaRepositories(basePackages = {
        "io.github.jokoframework.security",
        "io.github.jokoframework.myproject.basic.repositories"

})
@EntityScan(basePackages = {
        "io.github.jokoframework.security",
        "io.github.jokoframework.myproject.basic.entities"
})
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
