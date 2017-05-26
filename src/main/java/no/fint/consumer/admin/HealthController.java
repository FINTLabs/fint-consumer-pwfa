package no.fint.consumer.admin;

import no.fint.consumer.controller.Constants;
import no.fint.consumer.event.Actions;
import no.fint.consumer.event.ConsumerEventUtil;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/health", method = RequestMethod.GET)
public class HealthController {

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @RequestMapping
    public Event<String> healthCheck(@RequestHeader(value = Constants.HEADER_ORGID) String orgId,
                                     @RequestHeader(value = Constants.HEADER_CLIENT) String client) {
        Event<String> event = new Event<>(orgId, Constants.SOURCE, Actions.HEALTH.name(), client);
        Optional<Event> health = consumerEventUtil.healthCheck(event);

        if (health.isPresent()) {
            return new Event<>(health.get());
        } else {
            event.setMessage("No response from adapter");
            return event;
        }

    }
}
