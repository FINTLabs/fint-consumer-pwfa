package no.fint.consumer.service

import no.fint.event.model.Event
import spock.lang.Specification

class SubscriberServiceSpec extends Specification {
    private SubscriberService subscriberService

    void setup() {
        subscriberService = new SubscriberService()
    }

    def "No exception is thrown when receiving event"() {
        when:
        subscriberService.recieve(new Event(corrId: '123'))

        then:
        noExceptionThrown()
    }
}
