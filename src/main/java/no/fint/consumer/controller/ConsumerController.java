package no.fint.consumer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import no.fint.consumer.controller.dto.Dog;
import no.fint.consumer.event.Actions;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ConsumerController {

    @Autowired
    private FintEvents fintEvents;

    @GetMapping("dogs")
    public List<FintResource<Dog>> getAllDogs(@RequestHeader(value = Constants.HEADER_ORGID) String orgId,
                                              @RequestHeader(value = Constants.HEADER_CLIENT) String client) throws InterruptedException {
        Event<Void> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_DOGS.name(), client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue(Consumer.TEMP_QUEUE_PREFIX + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(1, TimeUnit.MINUTES);
        return EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Dog>>>() {
        });
    }

}
