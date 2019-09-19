package no.fint.consumer.models

import no.fint.audit.FintAuditService
import no.fint.consumer.config.ConsumerProps
import no.fint.consumer.models.owner.OwnerCacheService
import no.fint.consumer.models.owner.OwnerController
import no.fint.consumer.models.owner.OwnerLinker
import no.fint.event.model.HeaderConstants
import no.fint.model.pwfa.pwfa.Identifikator
import no.fint.model.resource.pwfa.pwfa.OwnerResource
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc

class OwnerControllerSpec extends MockMvcSpecification {
    private OwnerController controller
    private OwnerLinker linker
    private OwnerCacheService cacheService

    private FintAuditService auditService
    private ConsumerProps props

    private MockMvc mockMvc

    private List<OwnerResource> owners

    void setup() {
        linker = Mock()
        cacheService = Mock()
        auditService = Mock()
        props = Mock()
        controller = new OwnerController(cacheService: cacheService, linker: linker, fintAuditService: auditService, props: props)
        mockMvc = standaloneSetup(controller)

        owners = [new OwnerResource(id: new Identifikator(identifikatorverdi: '1'), name: 'Ole')]
    }

    def "Get all owners"() {
        when:
        def response = mockMvc.perform(get('/owner')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test'))


        then:
        1 * cacheService.getAll('mock.no') >> owners
        1 * linker.toResources(owners) >> owners
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$._embedded._entries[0].name').value(equalTo('Ole')))
    }

    def "Get owner"() {
        when:
        def response = mockMvc.perform(get('/owner/id/1')
                .header(HeaderConstants.ORG_ID, 'mock.no')
                .header(HeaderConstants.CLIENT, 'test'))

        then:
        1 * cacheService.getOwnerById('mock.no', '1') >> Optional.of(owners[0])
        1 * linker.toResource(_ as OwnerResource) >> owners[0]
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name').value(equalTo('Ole')))
    }

}
