package no.fint.consumer.event;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import no.fint.events.annotations.FintEventListener;
import no.fint.events.queue.QueueType;
import no.fint.model.relation.FintResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    public static final String TEMP_QUEUE_PREFIX = "pwfa-";

    @Autowired
    private FintEvents fintEvents;

    @FintEventListener(type = QueueType.UPSTREAM)
    public void receive(Event<FintResource> event) {
        log.info("Upstream event: {}", event);
        fintEvents.getTempQueue(TEMP_QUEUE_PREFIX + event.getCorrId()).offer(event);
    }

}
