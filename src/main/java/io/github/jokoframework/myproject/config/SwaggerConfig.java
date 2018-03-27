package io.github.jokoframework.myproject.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import io.github.jokoframework.myproject.constants.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableSwagger
@Profile(value = { "default" })
public class SwaggerConfig {

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo())
                .includePatterns(ApiPaths.API_PATTERN);

    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Joko-Starter-Kit API", "", "", "joko@sodep.com.py",
                "Joko-Starter-Kit API Licence Type", "Apache 2.0");
        return apiInfo;
    }
}