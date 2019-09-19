package no.fint.consumer.models.owner;

import no.fint.model.resource.Link;
import no.fint.model.resource.pwfa.pwfa.OwnerResource;
import no.fint.model.resource.pwfa.pwfa.OwnerResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;


@Component
public class OwnerLinker extends FintLinker<OwnerResource> {

    public OwnerLinker() {
        super(OwnerResource.class);
    }

    public void mapLinks(OwnerResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public OwnerResources toResources(Collection<OwnerResource> collection) {
        OwnerResources resources = new OwnerResources();
        collection.stream().map(this::toResource).forEach(resources::addResource);
        resources.addSelf(Link.with(self()));
        return resources;
    }

    @Override
    public String getSelfHref(OwnerResource owner) {
        if (!isNull(owner.getId()) && !isEmpty(owner.getId().getIdentifikatorverdi())) {
            return createHrefWithId(owner.getId().getIdentifikatorverdi(), "id");
        }
        
        return null;
    }
    
}

