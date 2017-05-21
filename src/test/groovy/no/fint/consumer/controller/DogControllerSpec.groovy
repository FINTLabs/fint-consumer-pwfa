package no.fint.consumer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.consumer.event.Actions
import no.fint.event.model.Event
import no.fint.events.FintEvents
import no.fint.model.relation.FintResource
import no.fint.pwfa.model.Dog
import no.fint.test.utils.MockMvcSpecification
import org.redisson.api.RBlockingQueue
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.util.concurrent.TimeUnit

class DogControllerSpec extends MockMvcSpecification {
    private DogController consumerController
    private FintEvents fintEvents
    private RBlockingQueue<Event<FintResource>> tempQueue
    private MockMvc mockMvc
    private ObjectMapper objectMapper

    void setup() {
        tempQueue = Mock(RBlockingQueue)
        fintEvents = Mock(FintEvents) {
            getTempQueue(_ as String) >> tempQueue
        }
        consumerController = new DogController(fintEvents: fintEvents)
        mockMvc = MockMvcBuilders.standaloneSetup(consumerController).build()
        objectMapper = new ObjectMapper()
    }

    def "Get all dogs"() {
        given:
        def event = new Event('mock.no', 'test', Actions.GET_ALL_DOGS.name(), 'test')
        event.setData([FintResource.with(new Dog('12345','Lykke', 'Springer'))])
        def json = objectMapper.writeValueAsString(event)

        when:
        def response = mockMvc.perform(get('/dogs')
                .header(Constants.HEADER_ORGID, 'mock.no')
                .header(Constants.HEADER_CLIENT, 'test')
        )

        then:
        1 * fintEvents.sendDownstream('mock.no', _ as Event)
        1 * tempQueue.poll(1, TimeUnit.MINUTES) >> objectMapper.readValue(json, Event)
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].resource.breed').value(equalTo('Springer')))
    }
}
