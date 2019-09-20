package no.fint.consumer.models.dog;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

import no.fint.cache.CacheService;
import no.fint.consumer.config.Constants;
import no.fint.consumer.config.ConsumerProps;
import no.fint.consumer.event.ConsumerEventUtil;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.relations.FintResourceCompatibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import no.fint.model.pwfa.pwfa.Dog;
import no.fint.model.resource.pwfa.pwfa.DogResource;
import no.fint.model.pwfa.pwfa.PwfaActions;

@Slf4j
@Service
public class DogCacheService extends CacheService<DogResource> {

    public static final String MODEL = Dog.class.getSimpleName().toLowerCase();

    @Value("${fint.consumer.compatibility.fintresource:true}")
    private boolean checkFintResourceCompatibility;

    @Autowired
    private FintResourceCompatibility fintResourceCompatibility;

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @Autowired
    private ConsumerProps props;

    @Autowired
    private DogLinker linker;

    private JavaType javaType;

    private ObjectMapper objectMapper;

    public DogCacheService() {
        super(MODEL, PwfaActions.GET_ALL_DOG, PwfaActions.UPDATE_DOG);
        objectMapper = new ObjectMapper();
        javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, DogResource.class);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @PostConstruct
    public void init() {
        props.getAssets().forEach(this::createCache);
    }

    @Scheduled(initialDelayString = Constants.CACHE_INITIALDELAY_DOG, fixedRateString = Constants.CACHE_FIXEDRATE_DOG)
    public void populateCacheAll() {
        props.getAssets().forEach(this::populateCache);
    }

    public void rebuildCache(String orgId) {
		flush(orgId);
		populateCache(orgId);
	}

    @Override
    public void populateCache(String orgId) {
		log.info("Populating Dog cache for {}", orgId);
        Event event = new Event(orgId, Constants.COMPONENT, PwfaActions.GET_ALL_DOG, Constants.CACHE_SERVICE);
        consumerEventUtil.send(event);
    }


    public Optional<DogResource> getDogById(String orgId, String id) {
        return getOne(orgId, (resource) -> Optional
                .ofNullable(resource)
                .map(DogResource::getId)
                .map(Identifikator::getIdentifikatorverdi)
                .map(_id -> _id.equals(id))
                .orElse(false));
    }


	@Override
    public void onAction(Event event) {
        List<DogResource> data;
        if (checkFintResourceCompatibility && fintResourceCompatibility.isFintResourceData(event.getData())) {
            log.info("Compatibility: Converting FintResource<DogResource> to DogResource ...");
            data = fintResourceCompatibility.convertResourceData(event.getData(), DogResource.class);
        } else {
            data = objectMapper.convertValue(event.getData(), javaType);
        }
        data.forEach(linker::mapLinks);
        if (PwfaActions.valueOf(event.getAction()) == PwfaActions.UPDATE_DOG) {
            if (event.getResponseStatus() == ResponseStatus.ACCEPTED || event.getResponseStatus() == ResponseStatus.CONFLICT) {
                add(event.getOrgId(), data);
                log.info("Added {} elements to cache for {}", data.size(), event.getOrgId());
            } else {
                log.debug("Ignoring payload for {} with response status {}", event.getOrgId(), event.getResponseStatus());
            }
        } else {
            update(event.getOrgId(), data);
            log.info("Updated cache for {} with {} elements", event.getOrgId(), data.size());
        }
    }
}
