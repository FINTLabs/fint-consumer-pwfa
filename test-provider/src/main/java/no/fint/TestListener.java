package no.fint;

import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.events.FintEvents;
import no.fint.events.FintEventsHealth;
import no.fint.events.annotations.FintEventListener;
import no.fint.events.queue.QueueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private FintEventsHealth fintEventsHealth;

    @FintEventListener(type = QueueType.DOWNSTREAM)
    public void recieve(Event event) {
        if (event.isHealthCheck()) {
            event.addObject(new Health("test-provider", HealthStatus.RECEIVED_IN_PROVIDER_FROM_CONSUMER));
            event.addObject(new Health("test-provider", HealthStatus.SENT_FROM_PROVIDER_TO_ADAPTER));
            event.addObject(new Health("test-adapter", HealthStatus.APPLICATION_HEALTHY));
            event.addObject(new Health("test-provider", HealthStatus.RECEIVED_IN_PROVIDER_FROM_ADAPTER));
            event.addObject(new Health("test-provider", HealthStatus.SENT_FROM_PROVIDER_TO_CONSUMER));

            event.setStatus(Status.TEMP_UPSTREAM_QUEUE);
            fintEventsHealth.respondHealthCheck(event.getCorrId(), event);
        }
    }

}
