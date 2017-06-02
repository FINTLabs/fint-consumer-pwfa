package no.fint.consumer.controller

import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
import org.springframework.test.web.servlet.MockMvc

import java.util.concurrent.TimeUnit

class OwnerControllerSpec extends MockMvcSpecification {
    private OwnerController controller
    private FintEvents fintEvents
    private RBlockingQueue queue
    private MockMvc mockMvc

    void setup() {
        queue = Mock(RBlockingQueue)
        fintEvents = Mock(FintEvents) {
            getTempQueue(_ as String) >> queue
        }
        controller = new OwnerController(fintEvents: fintEvents)
        mockMvc = standaloneSetup(controller)
    }

    def "GET all owners"() {
        when:
        def response = mockMvc.perform(get('/owners')
                .header(Constants.HEADER_ORGID, 'rogfk.no')
                .header(Constants.HEADER_CLIENT, 'test'))


        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> new Event<>()
        response.andExpect(status().isOk())
    }

    def "Return 500 internal server error when there is a null response from the queue"() {
        when:
        def response = mockMvc.perform(get('/owners')
                .header(Constants.HEADER_ORGID, 'rogfk.no')
                .header(Constants.HEADER_CLIENT, 'test'))


        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> null
        response.andExpect(status().isInternalServerError())
    }

    def "GET owner"() {
        when:
        def response = mockMvc.perform(get('/owners/1')
                .header(Constants.HEADER_ORGID, 'rogfk.no')
                .header(Constants.HEADER_CLIENT, 'test'))

        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> new Event<>()
        response.andExpect(status().isOk())
    }
}
