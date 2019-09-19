package no.fint.consumer.admin;

import no.fint.consumer.config.Constants;
import no.fint.consumer.config.ConsumerProps;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.events.FintEvents;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ConsumerProps props;

    @Autowired
    private FintEvents fintEvents;

    @PostMapping("/organization/orgIds/{orgId}")
    public ResponseEntity registerOrganization(@PathVariable String orgId) {
        if (props.getAssets().contains(orgId)) {
            return ResponseEntity.badRequest().body(String.format("OrgId %s is already registered", orgId));
        } else {
            Event event = new Event(orgId, Constants.COMPONENT, DefaultActions.REGISTER_ORG_ID, Constants.COMPONENT_CONSUMER);
            fintEvents.sendDownstream(event);

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
