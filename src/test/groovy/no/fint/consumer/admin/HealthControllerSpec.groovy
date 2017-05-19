package no.fint.consumer.admin

import no.fint.consumer.controller.Constants
import no.fint.consumer.event.Actions
import no.fint.consumer.event.ConsumerEventUtil
import no.fint.event.model.Event
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HealthControllerSpec extends MockMvcSpecification {
    private HealthController healthController
    private MockMvc mockMvc
    private ConsumerEventUtil consumerEventUtil

    void setup() {
        consumerEventUtil = Mock(ConsumerEventUtil)
        healthController = new HealthController(consumerEventUtil: consumerEventUtil)
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build()
    }

    def "Check response on healthcheck"() {

        when:
        def response = mockMvc.perform(get("/health").header(Constants.HEADER_ORGID, "mock.no").header(Constants.HEADER_CLIENT, "mock"))

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.of(new Event(action: Actions.HEALTH))
        response.andExpect(status().isOk()).andExpect(jsonPath('$.action').value(equalTo("HEALTH")))
    }

    def "Check response on healthcheck is empty"() {

        when:
        def response = mockMvc.perform(get("/health").header(Constants.HEADER_ORGID, "mock.no").header(Constants.HEADER_CLIENT, "mock"))

        then:
        1 * consumerEventUtil.healthCheck(_ as Event) >> Optional.empty()
        response.andExpect(status().isOk()).andExpect(jsonPath('$.action').value(equalTo("HEALTH")))
        .andExpect(jsonPath('$.message').value(equalTo("No response from adapter")))
    }
}
