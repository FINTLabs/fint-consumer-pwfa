package no.fint.consumer.models.owner;

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
import no.fint.relations.FintResourceCompatibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import no.fint.model.pwfa.pwfa.Owner;
import no.fint.model.resource.pwfa.pwfa.OwnerResource;
import no.fint.model.pwfa.pwfa.PwfaActions;
import no.fint.model.pwfa.pwfa.Identifikator;

@Slf4j
@Service
public class OwnerCacheService extends CacheService<OwnerResource> {

    public static final String MODEL = Owner.class.getSimpleName().toLowerCase();

    @Value("${fint.consumer.compatibility.fintresource:true}")
    private boolean checkFintResourceCompatibility;

    @Autowired
    private FintResourceCompatibility fintResourceCompatibility;

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @Autowired
    private ConsumerProps props;

    @Autowired
    private OwnerLinker linker;

    private JavaType javaType;

    private ObjectMapper objectMapper;

    public OwnerCacheService() {
        super(MODEL, PwfaActions.GET_ALL_OWNER, PwfaActions.UPDATE_OWNER);
        objectMapper = new ObjectMapper();
        javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, OwnerResource.class);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @PostConstruct
    public void init() {
        props.getAssets().forEach(this::createCache);
    }

    @Scheduled(initialDelayString = Constants.CACHE_INITIALDELAY_OWNER, fixedRateString = Constants.CACHE_FIXEDRATE_OWNER)
    public void populateCacheAll() {
        props.getAssets().forEach(this::populateCache);
    }

    public void rebuildCache(String orgId) {
		flush(orgId);
		populateCache(orgId);
	}

    @Override
    public void populateCache(String orgId) {
		log.info("Populating Owner cache for {}", orgId);
        Event event = new Event(orgId, Constants.COMPONENT, PwfaActions.GET_ALL_OWNER, Constants.CACHE_SERVICE);
        consumerEventUtil.send(event);
    }


    public Optional<OwnerResource> getOwnerById(String orgId, String id) {
        return getOne(orgId, (resource) -> Optional
                .ofNullable(resource)
                .map(OwnerResource::getId)
                .map(Identifikator::getIdentifikatorverdi)
                .map(_id -> _id.equals(id))
                .orElse(false));
    }


	@Override
    public void onAction(Event event) {
        List<OwnerResource> data;
        if (checkFintResourceCompatibility && fintResourceCompatibility.isFintResourceData(event.getData())) {
            log.info("Compatibility: Converting FintResource<OwnerResource> to OwnerResource ...");
            data = fintResourceCompatibility.convertResourceData(event.getData(), OwnerResource.class);
        } else {
            data = objectMapper.convertValue(event.getData(), javaType);
        }
        data.forEach(linker::mapLinks);
        if (PwfaActions.valueOf(event.getAction()) == PwfaActions.UPDATE_OWNER) {
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
