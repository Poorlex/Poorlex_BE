package com.poolex.poolex.config.event;

import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;

public class Events {

    private static ApplicationEventPublisher publisher;

    private Events() {

    }

    static void setPublisher(final ApplicationEventPublisher publisher) {
        Events.publisher = publisher;
    }

    public static void raise(final Object event) {
        if (Objects.nonNull(publisher)) {
            publisher.publishEvent(event);
        }
    }
}
