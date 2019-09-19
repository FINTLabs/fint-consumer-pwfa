package no.fint.consumer.models

import no.fint.audit.FintAuditService
import no.fint.consumer.config.ConsumerProps
import no.fint.consumer.models.dog.DogCacheService
import no.fint.consumer.models.dog.DogController
import no.fint.consumer.models.dog.DogLinker
import no.fint.event.model.HeaderConstants
import no.fint.model.pwfa.pwfa.Identifikator
import no.fint.model.resource.pwfa.pwfa.DogResource
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class DogControllerSpec extends MockMvcSpecification {
    private DogController controller
    private DogLinker linker
    private DogCacheService cacheService

    private ConsumerProps props
    private FintAuditService auditService

    private MockMvc mockMvc

    private List<DogResource> dogs

    void setup() {
        linker = Mock()
        cacheService = Mock()
        auditService = Mock()
        props = Mock()
        controller = new DogController(linker: linker, cacheService: cacheService, fintAuditService: auditService, props: props)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        dogs = [new DogResource(id: new Identifikator(identifikatorverdi: '12345'), name: 'Lykke', breed: 'Springer')]
    }

    def "Get all dogs"() {
        when:
        def response = mockMvc.perform(get('/dog')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test')
        )

        then:
        1 * cacheService.getAll('mock.no') >> dogs
        1 * linker.toResources(dogs) >> dogs
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$._embedded._entries[0].breed').value(equalTo('Springer')))
    }

    def "Get dog"() {
        when:
        def response = mockMvc.perform(get('/dog/id/1')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test')
        )

        then:
        1 * cacheService.getDogById('mock.no', '1') >> Optional.of(dogs[0])
        1 * linker.toResource(_ as DogResource) >> dogs[0]
        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath('$.breed').value(equalTo('Springer')))
    }

}
