package no.fint.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.consumer.config.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification


@SpringBootTest(classes = [Config, ObjectMapper])
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
