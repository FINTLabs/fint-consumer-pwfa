package no.fint.consumer.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.annotations.FintEventListener;
import no.fint.events.queue.QueueType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriberService {

    @FintEventListener(type = QueueType.UPSTREAM)
    public void recieve(Event event) {
        log.info("Event received: {}", event.getCorrId());
        // List<FintResource<Personalressurs>> personalressursList = EventUtil.convertEventData(event, new TypeReference<List<FintResource<Personalressurs>>>() {});
    }

}
