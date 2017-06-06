package no.fint.consumer.admin;

import lombok.AccessLevel;
import lombok.Setter;
import no.fint.consumer.controller.Constants;
import no.fint.consumer.event.EventListener;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@RestController
@RequestMapping(value = "/organization", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationController {

    @Setter(AccessLevel.PACKAGE)
    private Map<String, Long> orgIds = new ConcurrentHashMap<>();

    @Autowired
    private FintEvents fintEvents;

    @PostMapping("/orgIds/{orgId}")
    public ResponseEntity registerOrganization(@PathVariable String orgId) {
        if (orgIds.containsKey(orgId)) {
            return ResponseEntity.badRequest().body(String.format("OrgId %s is already registered", orgId));
        } else {
            Event event = new Event(orgId, Constants.SOURCE, DefaultActions.REGISTER_ORG_ID.name(), "pwfa");
            fintEvents.sendDownstream("system", event);

            fintEvents.registerUpstreamListener(EventListener.class, orgId);
            orgIds.put(orgId, System.currentTimeMillis());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
            return ResponseEntity.created(location).build();
        }
    }
}
