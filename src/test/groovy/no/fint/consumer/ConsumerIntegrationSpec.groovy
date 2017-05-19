package no.fint.consumer

import no.fint.consumer.admin.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles('test')
@SpringBootTest
class ConsumerIntegrationSpec extends Specification {

    @Autowired
    private AdminService adminService

    def "Initialize consumer"() {
        when:
        def adminServiceCreated = (adminService != null)

        then:
        adminServiceCreated
    }

}
