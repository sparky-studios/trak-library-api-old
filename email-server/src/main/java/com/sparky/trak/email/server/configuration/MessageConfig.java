package com.sparky.trak.email.server.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasenames("classpath:i18n/messages", "classpath:i18n/exception");
        reloadableResourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());

        return reloadableResourceBundleMessageSource;
    }
}
