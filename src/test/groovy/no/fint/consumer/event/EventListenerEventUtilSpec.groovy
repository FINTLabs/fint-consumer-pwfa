package no.fint.consumer.event

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.events.FintEventsHealth
import spock.lang.Specification

class EventListenerEventUtilSpec extends Specification {
    private ConsumerEventUtil consumerEventUtil
    private FintEvents fintEvents
    private FintAuditService fintAuditService
    private FintEventsHealth fintEventsHealth

    void setup() {
        fintEvents = Mock(FintEvents)
        fintEventsHealth = Mock(FintEventsHealth)
        fintAuditService = Mock(FintAuditService)
        consumerEventUtil = new ConsumerEventUtil(fintEventsHealth: fintEventsHealth, fintEvents: fintEvents, fintAuditService: fintAuditService)
    }

    def "Send and receive health check"() {
        given:
        def event = new Event(orgId: 'rogfk.no', corrId: '123')

        when:
        def response = consumerEventUtil.healthCheck(event)

        then:
        3 * fintAuditService.audit(_ as Event)
        1 * fintEventsHealth.sendHealthCheck('rogfk.no', '123', event) >> new Event<>()
        response.isPresent()
    }

    def "Send downstream event"() {
        given:
        def event = new Event(orgId: 'rogfk.no')

        when:
        consumerEventUtil.send(event)

        then:
        3 * fintAuditService.audit(_ as Event)
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
    }
}
