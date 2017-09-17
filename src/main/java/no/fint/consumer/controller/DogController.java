package no.fint.consumer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import no.fint.consumer.event.Actions;
import no.fint.consumer.event.EventListener;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.HeaderConstants;
import no.fint.events.FintEvents;
import no.fint.model.relation.FintResource;
import no.fint.pwfa.model.Dog;
import org.redisson.api.RBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@RequestMapping(value = "/dogs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DogController {

    private final TypeReference<List<FintResource<Dog>>> dogTypeReference = new TypeReference<List<FintResource<Dog>>>() {
    };

    @Autowired
    private FintEvents fintEvents;

    @Autowired
    private DogAssembler assembler;

    @GetMapping
    public ResponseEntity getAllDogs(@RequestHeader(HeaderConstants.ORG_ID) String orgId,
                                     @RequestHeader(HeaderConstants.CLIENT) String client) throws InterruptedException {
        Event<Void> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_ALL_DOGS, client);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue(EventListener.TEMP_QUEUE_PREFIX + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(1, TimeUnit.MINUTES);
        if (receivedEvent == null) {
            return getErrorResponse();
        } else {
            List<FintResource<Dog>> fintResources = EventUtil.convertEventData(receivedEvent, dogTypeReference);
            return assembler.resources(fintResources);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getDog(@PathVariable String id,
                                 @RequestHeader(HeaderConstants.ORG_ID) String orgId,
                                 @RequestHeader(HeaderConstants.CLIENT) String client) throws InterruptedException {
        Event<Void> event = new Event<>(orgId, Constants.SOURCE, Actions.GET_DOG, client);
        event.setQuery(id);
        fintEvents.sendDownstream(orgId, event);

        RBlockingQueue<Event<FintResource>> tempQueue = fintEvents.getTempQueue(EventListener.TEMP_QUEUE_PREFIX + event.getCorrId());
        Event<FintResource> receivedEvent = tempQueue.poll(1, TimeUnit.MINUTES);
        if (receivedEvent == null) {
            return getErrorResponse();
        } else {
            List<FintResource<Dog>> fintResources = EventUtil.convertEventData(receivedEvent, dogTypeReference);
            return assembler.resource(fintResources.get(0));
        }
    }

    private ResponseEntity getErrorResponse() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The request timed out before a response was received from the adapter");
    }
}
