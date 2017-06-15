package no.fint.consumer.controller

import no.fint.consumer.event.Actions
import no.fint.event.model.Event
import no.fint.event.model.HeaderConstants
import no.fint.events.FintEvents
import no.fint.model.relation.FintResource
import no.fint.pwfa.model.Owner
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
import org.springframework.test.web.servlet.MockMvc

import java.util.concurrent.TimeUnit

class OwnerControllerSpec extends MockMvcSpecification {
    private OwnerController controller
    private FintEvents fintEvents
    private RBlockingQueue queue
    private MockMvc mockMvc

    private Event<Owner> event

    void setup() {
        queue = Mock(RBlockingQueue)
        fintEvents = Mock(FintEvents) {
            getTempQueue(_ as String) >> queue
        }
        controller = new OwnerController(fintEvents: fintEvents)
        mockMvc = standaloneSetup(controller)

        event = new Event('mock.no', 'test', Actions.GET_ALL_OWNERS.name(), 'test')
        event.setData([FintResource.with(new Owner('1', 'Ole'))])
    }

    def "Get all owners"() {
        when:
        def response = mockMvc.perform(get('/owners')
                .header(HeaderConstants.ORG_ID, 'rogfk.no')
                .header(HeaderConstants.CLIENT, 'test'))


        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> event
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].resource.name').value(equalTo('Ole')))
    }

    def "Get owner"() {
        when:
        def response = mockMvc.perform(get('/owners/1')
                .header(HeaderConstants.ORG_ID, 'rogfk.no')
                .header(HeaderConstants.CLIENT, 'test'))

        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> event
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].resource.name').value(equalTo('Ole')))
    }

    def "Return status code 500 if response event is null"() {
        when:
        def response = mockMvc.perform(get('/owners')
                .header(HeaderConstants.ORG_ID, 'rogfk.no')
                .header(HeaderConstants.CLIENT, 'test'))


        then:
        1 * fintEvents.sendDownstream('rogfk.no', _ as Event)
        1 * queue.poll(1, TimeUnit.MINUTES) >> null
        response.andExpect(status().isInternalServerError())
    }
}
