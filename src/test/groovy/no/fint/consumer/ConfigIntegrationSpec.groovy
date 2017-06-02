package no.fint.consumer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification


@SpringBootTest(classes = Config)
class ConfigIntegrationSpec extends Specification {

    @Qualifier("linkMapper")
    @Autowired
    private Map<String, String> linkMapper

    def "Load linkmapper configuration"() {
        when:
        def size = linkMapper.size()

        then:
        size == 2
    }
}
