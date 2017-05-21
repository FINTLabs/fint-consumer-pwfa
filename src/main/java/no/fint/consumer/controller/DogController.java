package no.fint.consumer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import no.fint.consumer.event.Actions;
import no.fint.consumer.event.EventListener;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import no.fint.pwfa.model.Dog;
import no.fint.relations.annotations.FintRelations;
import no.fint.relations.annotations.FintSelf;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@FintSelf(type = Dog.class, property = "id")
@RestController
@RequestMapping(value = "/dogs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DogController {

    @Autowired
    private FintEvents fintEvents;

    @FintRelations
    @GetMapping
    public ResponseEntity getAllDogs(@RequestHeader(value = Constants.HEADER_ORGID) String orgId,
                                     @RequestHeader(value = Constants.HEADER_CLIENT) String client) throws InterruptedException {
        Event<Void> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_DOGS.name(), client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue(EventListener.TEMP_QUEUE_PREFIX + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(1, TimeUnit.MINUTES);
        return ResponseEntity.ok(EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Dog>>>() {
        }));
    }

    @FintRelations
    @GetMapping("/{id}")
    public ResponseEntity getDog(@PathVariable String id,
                                              @RequestHeader(value = Constants.HEADER_ORGID) String orgId,
                                              @RequestHeader(value = Constants.HEADER_CLIENT) String client) throws InterruptedException {
        Event<Void> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_DOG.name(), client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue(EventListener.TEMP_QUEUE_PREFIX + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(1, TimeUnit.MINUTES);
        return ResponseEntity.ok(EventUtil.convertEventData(receivedEvent, new TypeReference<List<FintResource<Dog>>>() {
        }));
    }


}
