package no.fint.consumer.admin

import no.fint.consumer.event.ConsumerEventUtil
import no.fint.event.model.Event
import spock.lang.Specification

class AdminServiceSpec extends Specification {
    private AdminService adminService
    private ConsumerEventUtil consumerEventUtil

    void setup() {
        consumerEventUtil = Mock(ConsumerEventUtil)
        adminService = new AdminService(consumerEventUtil: consumerEventUtil)
    }

    def "Return 'No response received' when empty response from Event queue"() {
        when:
        def health = adminService.healthCheck('orgId', 'client')

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.empty()
        health.corrId != null
        health.status == 'No response received'
    }

    def "Return 'Empty data' if Event data is empty"() {
        when:
        def health = adminService.healthCheck('orgId', 'client')

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.of(new Event(corrId: 'corrId'))
        health.corrId == 'corrId'
        health.status == 'Empty data'
    }

    def "Return first element of response when Event contains data"() {
        when:
        def health = adminService.healthCheck('orgId', 'client')

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.of(new Event(corrId: 'corrId', data: ['test']))
        health.corrId == 'corrId'
        health.status == 'test'
    }
}
