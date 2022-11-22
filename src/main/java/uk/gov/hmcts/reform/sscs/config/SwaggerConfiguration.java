package uk.gov.hmcts.reform.sscs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customImplementation() {
        return new OpenAPI()
            .info(new Info()
                .title("ccd-callback-orchestrator")
                .description("SSCS CCD Callback Orchestrator")
                .version("1.0.0")
                .contact(new Contact().name("SSCS")
                    .url("http://sscs.net/")
                    .email("sscs@hmcts.net")));
    }
}
