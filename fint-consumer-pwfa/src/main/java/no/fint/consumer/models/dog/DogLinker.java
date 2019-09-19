package no.fint.consumer.models.dog;

import no.fint.model.resource.Link;
import no.fint.model.resource.pwfa.pwfa.DogResource;
import no.fint.model.resource.pwfa.pwfa.DogResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;


@Component
public class DogLinker extends FintLinker<DogResource> {

    public DogLinker() {
        super(DogResource.class);
    }

    public void mapLinks(DogResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public DogResources toResources(Collection<DogResource> collection) {
        DogResources resources = new DogResources();
        collection.stream().map(this::toResource).forEach(resources::addResource);
        resources.addSelf(Link.with(self()));
        return resources;
    }

    @Override
    public String getSelfHref(DogResource dog) {
        if (!isNull(dog.getId()) && !isEmpty(dog.getId().getIdentifikatorverdi())) {
            return createHrefWithId(dog.getId().getIdentifikatorverdi(), "id");
        }
        
        return null;
    }
    
}

