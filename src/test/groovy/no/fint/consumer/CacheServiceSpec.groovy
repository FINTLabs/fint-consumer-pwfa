package no.fint.consumer

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import no.fint.cache.FintCache
import no.fint.consumer.utils.CacheUri
import spock.lang.Specification

class CacheServiceSpec extends Specification {
    private CacheService cacheService
    private String cacheUri

    void setup() {
        cacheUri = CacheUri.create('rogfk.no', 'person');

        FintCache<String> cache = new FintCache<>()
        cacheService = new CacheService(caches: ImmutableMap.of(cacheUri, cache))
        cacheService.update(cacheUri, Lists.newArrayList('123', '234'))
    }

    def "Get last updated"() {
        when:
        def lastUpdated = cacheService.getLastUpdated(cacheUri)

        then:
        lastUpdated != null
    }

    def "Get all persisted objects"() {
        when:
        def objects = cacheService.getAll(cacheUri)

        then:
        objects.size() == 2
    }

    def "Get all objects with cacheUri, return empty when no cache is found"() {
        when:
        def objects = cacheService.getAll('unknown-uri')

        then:
        objects.size() == 0
    }

    def "Get all objects with cacheUri, return empty when null cacheUri"() {
        when:
        def objects = cacheService.getAll(null)

        then:
        objects.size() == 0
    }

    def "Get all objects with cacheUri and timestamp"() {
        when:
        def objects = cacheService.getAll(cacheUri, (System.currentTimeMillis() - 5000L))

        then:
        objects.size() == 2
    }

    def "Get all objects with cacheUri and timestamp, return empty when no cache is found"() {
        when:
        def objects = cacheService.getAll('unknown-uri', 0L)

        then:
        objects.size() == 0
    }

    def "Update cache"() {
        when:
        cacheService.update(cacheUri, ['123'])
        def objects = cacheService.getAll(cacheUri)

        then:
        objects.size() == 1
    }


}
