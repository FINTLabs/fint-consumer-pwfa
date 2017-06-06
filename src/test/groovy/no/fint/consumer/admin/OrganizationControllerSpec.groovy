package no.fint.consumer.admin

import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.test.utils.MockMvcSpecification
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class OrganizationControllerSpec extends MockMvcSpecification {
    private OrganizationController controller
    private FintEvents fintEvents
    private MockMvc mockMvc

    void setup() {
        fintEvents = Mock(FintEvents)
        controller = new OrganizationController(fintEvents: fintEvents)
        mockMvc = standaloneSetup(controller)
    }

    def "POST new organization"() {
        when:
        def response = mockMvc.perform(post('/organization/orgIds/123'))

        then:
        1 * fintEvents.sendDownstream('system', _ as Event)
        response.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, equalTo('http://localhost/organization/orgIds/123')))
    }

    def "POST new orgId, return bad request if orgId is already registered"() {
        given:
        controller.setOrgIds(['123': 123456L])

        when:
        def response = mockMvc.perform(post('/organization/orgIds/123'))

        then:
        response.andExpect(status().isBadRequest())
    }
}
