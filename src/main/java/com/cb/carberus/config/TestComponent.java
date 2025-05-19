package com.cb.carberus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Component
public class TestComponent {

    @Bean
    public CommandLineRunner printBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("📦 Listing all Spring Beans:");

            String[] beanNames = ctx.getBeanNamesForType(Repository.class);
            Arrays.sort(beanNames); // optional: alphabetically

            for (String beanName : beanNames) {
                System.out.println("🔹 " + beanName);
            }

            System.out.println("✅ Total Beans: " + beanNames.length);
        };
    }
}
