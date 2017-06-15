package no.fint.consumer.controller

import no.fint.consumer.event.Actions
import no.fint.event.model.Event
import no.fint.event.model.HeaderConstants
import no.fint.events.FintEvents
import no.fint.model.relation.FintResource
import no.fint.pwfa.model.Dog
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.util.concurrent.TimeUnit

class DogControllerSpec extends MockMvcSpecification {
    private DogController consumerController
    private FintEvents fintEvents
    private RBlockingQueue<Event<FintResource>> tempQueue
    private MockMvc mockMvc

    private Event<Dog> event

    void setup() {
        tempQueue = Mock(RBlockingQueue)
        fintEvents = Mock(FintEvents) {
            getTempQueue(_ as String) >> tempQueue
        }
        consumerController = new DogController(fintEvents: fintEvents)
        mockMvc = MockMvcBuilders.standaloneSetup(consumerController).build()

        event = new Event('mock.no', 'test', Actions.GET_ALL_DOGS.name(), 'test')
        event.setData([FintResource.with(new Dog('12345', 'Lykke', 'Springer'))])
    }

    def "Get all dogs"() {
        when:
        def response = mockMvc.perform(get('/dogs')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test')
        )

        then:
        1 * fintEvents.sendDownstream('mock.no', _ as Event)
        1 * tempQueue.poll(1, TimeUnit.MINUTES) >> event
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].resource.breed').value(equalTo('Springer')))
    }

    def "Get dog"() {
        when:
        def response = mockMvc.perform(get('/dogs/1')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test')
        )

        then:
        1 * fintEvents.sendDownstream('mock.no', _ as Event)
        1 * tempQueue.poll(1, TimeUnit.MINUTES) >> event
        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath('$[0].resource.breed').value(equalTo('Springer')))
    }

    def "Return status code 500 if response event is null"() {
        when:
        def response = mockMvc.perform(get('/dogs')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test'))

        then:
        1 * tempQueue.poll(1, TimeUnit.MINUTES) >> null
        response.andExpect(status().isInternalServerError())
    }
}
