package com.mtsaas.gateway.service.eureka;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EurekaClientShutdownHandler {

    @Autowired
    private EurekaClient eurekaClient;

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        // Explicitly shutdown Eureka client before closing the connection pool
        eurekaClient.shutdown();
    }
}