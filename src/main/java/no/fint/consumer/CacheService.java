package no.fint.consumer;

import lombok.extern.slf4j.Slf4j;
import no.fint.cache.Cache;
import no.fint.cache.FintCache;
import no.fint.cache.model.CacheObject;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CacheService<T> {
    protected Map<String, Cache<T>> caches = new HashMap<>();

    public long getLastUpdated(String cacheUri) {
        FintCache fintCache = (FintCache) caches.get(cacheUri);
        return fintCache.getLastUpdated();
    }

    public Optional<Cache<T>> getCache(String cacheUri) {
        if (cacheUri == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(caches.get(cacheUri));
    }

    public List<T> getAll(String cacheUri) {
        Optional<Cache<T>> cache = getCache(cacheUri);
        if (cache.isPresent()) {
            List<CacheObject<T>> cacheObjects = cache.get().get();
            return cacheObjects.stream().map(CacheObject::getObject).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public List<T> getAll(String cacheUri, long sinceTimestamp) {
        Optional<Cache<T>> cache = getCache(cacheUri);
        if (cache.isPresent()) {
            List<CacheObject<T>> cacheObjects = cache.get().getSince(sinceTimestamp);
            return cacheObjects.stream().map(CacheObject::getObject).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void update(String cacheUri, List<T> testObjects) {
        Optional<Cache<T>> cache = getCache(cacheUri);
        cache.ifPresent(testObjectCache -> testObjectCache.update(testObjects));
    }
}
