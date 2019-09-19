package no.fint.consumer.admin;

import no.fint.consumer.controller.Constants;
import no.fint.consumer.event.EventListener;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping(value = "/demo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DemoController {

    @PostMapping("/organization/orgIds/{orgId}")
    public ResponseEntity registerOrganization(@PathVariable String orgId) {
        if (orgIds.containsKey(orgId)) {
            return ResponseEntity.badRequest().body(String.format("OrgId %s is already registered", orgId));
        } else {
            Event event = new Event(orgId, Constants.SOURCE, DefaultActions.REGISTER_ORG_ID, Constants.CLIENT);
            fintEvents.sendDownstream("system", event);

            fintEvents.registerUpstreamListener(EventListener.class, orgId);
            orgIds.put(orgId, System.currentTimeMillis());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
            return ResponseEntity.created(location).build();
        }
    }

    @GetMapping("/organization/generateOrgId")
    public ResponseEntity generateOrganization() {
        String orgId = UUID.randomUUID().toString();
        ResponseEntity responseEntity = registerOrganization(orgId);
        return new ResponseEntity<>(orgId, responseEntity.getHeaders(), HttpStatus.OK);
    }
}
