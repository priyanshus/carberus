package com.cb.carberus.config;

import com.cb.carberus.security.AuditAwareImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    private UserContext userContext;

    public AuditConfig(UserContext userContext) {
        this.userContext = userContext;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditAwareImplementation(this.userContext);
    }
}
