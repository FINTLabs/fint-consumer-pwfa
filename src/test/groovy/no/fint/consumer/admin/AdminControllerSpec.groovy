package no.fint.consumer.admin

import no.fint.consumer.controller.Constants
import no.fint.consumer.event.ConsumerEventUtil
import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.test.utils.MockMvcSpecification
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AdminControllerSpec extends MockMvcSpecification {
    private AdminController controller
    private MockMvc mockMvc
    private FintEvents fintEvents
    private ConsumerEventUtil consumerEventUtil

    void setup() {
        fintEvents = Mock(FintEvents)
        consumerEventUtil = Mock(ConsumerEventUtil)
        controller = new AdminController(fintEvents: fintEvents, consumerEventUtil: consumerEventUtil)
        mockMvc = standaloneSetup(controller)
    }

    def "Check response on healthcheck"() {
        when:
        def response = mockMvc.perform(get("/admin/health").header(Constants.HEADER_ORGID, "mock.no").header(Constants.HEADER_CLIENT, "mock"))

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.of(new Event(action: DefaultActions.HEALTH.name()))
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.action')
                .value(equalTo("HEALTH")))
    }

    def "Check response on healthcheck is empty"() {
        when:
        def response = mockMvc.perform(get("/admin/health").header(Constants.HEADER_ORGID, "mock.no").header(Constants.HEADER_CLIENT, "mock"))

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.empty()
        response.andExpect(status().is5xxServerError())
                .andExpect(jsonPath('$.action').value(equalTo("HEALTH")))
                .andExpect(jsonPath('$.message').value(equalTo("No response from adapter")))
    }

    def "POST new organization"() {
        when:
        def response = mockMvc.perform(post('/admin/organization/orgIds/123'))

        then:
        1 * fintEvents.sendDownstream('system', _ as Event)
        response.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, equalTo('http://localhost/admin/organization/orgIds/123')))
    }

    def "Generate new orgId"() {
        when:
        def response = mockMvc.perform(get('/admin/organization/generateOrgId'))

        then:
        1 * fintEvents.sendDownstream('system', _ as Event)
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$').isString())
    }

    def "POST new orgId, return bad request if orgId is already registered"() {
        given:
        controller.setOrgIds(['123': 123456L])

        when:
        def response = mockMvc.perform(post('/admin/organization/orgIds/123'))

        then:
        response.andExpect(status().isBadRequest())
    }
}
