package no.fint.consumer.event

import no.fint.audit.FintAuditService
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.events.FintEventsHealth
import no.fint.events.HealthCheck
import spock.lang.Specification

class ConsumerEventUtilSpec extends Specification {
    private ConsumerEventUtil consumerEventUtil
    private FintEvents fintEvents
    private HealthCheck healthCheck
    private FintAuditService fintAuditService

    void setup() {
        fintEvents = Mock(FintEvents)
        healthCheck = Mock(HealthCheck)
        def fintEventsHealth = Mock(FintEventsHealth) {
            registerClient() >> healthCheck
        }
        fintAuditService = Mock(FintAuditService)
        consumerEventUtil = new ConsumerEventUtil(fintEventsHealth: fintEventsHealth, fintEvents: fintEvents, fintAuditService: fintAuditService)
    }

    def "Send and receive health check"() {
        given:
        def event = new Event(orgId: 'rogfk.no')

        when:
        consumerEventUtil.init()
        def response = consumerEventUtil.healthCheck(event)

        then:
        3 * fintAuditService.audit(_ as Event)
        1 * healthCheck.check(_ as Event) >> event
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
