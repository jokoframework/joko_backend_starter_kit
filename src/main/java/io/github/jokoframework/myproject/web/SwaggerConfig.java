package io.github.jokoframework.myproject.web;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API Configuration
 *
 * @author agimenez
 */
@Configuration
@EnableSwagger
public class SwaggerConfig {

    private SpringSwaggerConfig config;

    @Autowired
    public void setConfig(SpringSwaggerConfig config) {
        this.config = config;
    }

    @Bean
    public SwaggerSpringMvcPlugin getSwagger() {
        return new SwaggerSpringMvcPlugin(this.config).apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .includePatterns(APIPaths.API_PATTERN);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Starter Kit API",
                "Starter Kit Services",
                "Terms of Service", "soporte@XXXXX",
                "","");
    }

}