package com.sparkystudios.traklibrary.game.server.configuration;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.mediatype.hal.HalLinkDiscoverer;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class TrakHalJsonMediaTypeConfiguration implements HypermediaMappingInformation {

    private final LinkRelationProvider relProvider;
    private final ObjectProvider<CurieProvider> curieProvider;
    private final ObjectProvider<HalConfiguration> halConfiguration;
    @Qualifier("messageResolver")
    private final MessageResolver resolver;

    @Bean
    public LinkDiscoverer halLinkDiscoverer() {
        return new HalLinkDiscoverer();
    }

    @Override
    public Module getJacksonModule() {
        return new Jackson2HalModule();
    }

    @Override
    @NonNull
    public ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(getJacksonModule());
        mapper.registerModule(new JSR353Module());

        mapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider,
                curieProvider.getIfAvailable(() -> CurieProvider.NONE), resolver, true,
                halConfiguration.getIfAvailable(HalConfiguration::new)));

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return mapper;
    }

    @Override
    @NonNull
    public List<MediaType> getMediaTypes() {
        return MediaType.parseMediaTypes("application/vnd.traklibrary.v1.hal+json");
    }
}
