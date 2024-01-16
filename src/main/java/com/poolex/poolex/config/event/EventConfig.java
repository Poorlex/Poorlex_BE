package com.poolex.poolex.config.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    public InitializingBean eventsInitializer(@Autowired ApplicationContext applicationContext) {
        return () -> Events.setPublisher(applicationContext);
    }
}
