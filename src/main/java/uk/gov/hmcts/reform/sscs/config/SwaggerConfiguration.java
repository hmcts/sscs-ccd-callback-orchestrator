package uk.gov.hmcts.reform.sscs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI ccdCallbackOrchestrator() {
        return new OpenAPI()
            .info(new Info().title("CCD Callback Orchestrator")
                .description("CCD Callback Orchestrator")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
