package no.fint.consumer.admin;

import no.fint.consumer.event.EventActions;
import no.fint.consumer.event.ConsumerEventUtil;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @Value("${springfox.title}")
    private String title;

    public Health healthCheck(String orgId, String client) {
        Event event = new Event(orgId, title, EventActions.HEALTH.name(), client);
        Optional<Event> upstreamEvent = consumerEventUtil.healthCheck(event);
        if (upstreamEvent.isPresent()) {
            List data = upstreamEvent.get().getData();
            if (data.size() > 0) {
                return new Health(upstreamEvent.get().getCorrId(), (String) upstreamEvent.get().getData().get(0));
            } else {
                return new Health(upstreamEvent.get().getCorrId(), "Empty data");
            }
        } else {
            return new Health(event.getCorrId(), "No response received");
        }
    }
}
