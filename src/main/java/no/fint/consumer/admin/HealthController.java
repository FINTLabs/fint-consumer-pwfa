package no.fint.consumer.admin;

import no.fint.consumer.controller.Constants;
import no.fint.consumer.event.ConsumerEventUtil;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.event.model.Health;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/health", method = RequestMethod.GET)
public class HealthController {

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @RequestMapping
    public ResponseEntity healthCheck(@RequestHeader(value = Constants.HEADER_ORGID) String orgId,
                                      @RequestHeader(value = Constants.HEADER_CLIENT) String client) {
        Event<Health> event = new Event<>(orgId, Constants.SOURCE, DefaultActions.HEALTH.name(), client);
        Optional<Event<Health>> health = consumerEventUtil.healthCheck(event);

        if (health.isPresent()) {
            return ResponseEntity.ok(health.get());
        } else {
            event.setMessage("No response from adapter");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(event);
        }
    }
}
