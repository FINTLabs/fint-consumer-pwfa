package no.fint.consumer.event

import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.events.FintEvents
import org.redisson.api.RBlockingQueue
import spock.lang.Specification

class EventListenerSpec extends Specification {
    private EventListener eventListener
    private FintEvents fintEvents
    private RBlockingQueue queue

    void setup() {
        queue = Mock(RBlockingQueue)
        fintEvents = Mock(FintEvents)
        eventListener = new EventListener(fintEvents: fintEvents)
    }

    def "Receive event"() {
        given:
        def event = new Event('rogfk.no', 'test', DefaultActions.HEALTH.name(), 'test')

        when:
        eventListener.receive(event)

        then:
        1 * fintEvents.getTempQueue(EventListener.TEMP_QUEUE_PREFIX + event.corrId) >> queue
        1 * queue.offer(event)
    }
}
