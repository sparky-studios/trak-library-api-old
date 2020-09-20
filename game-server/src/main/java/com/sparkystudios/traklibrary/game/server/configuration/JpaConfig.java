package com.sparkystudios.traklibrary.game.server.configuration;

import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableJpaRepositories("com.sparkystudios.traklibrary.game.repository")
public class JpaConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers) {
        handlerMethodArgumentResolvers.add(new SpecificationArgumentResolver());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new JsonMergePatchHttpMessageConverter());
    }
}
