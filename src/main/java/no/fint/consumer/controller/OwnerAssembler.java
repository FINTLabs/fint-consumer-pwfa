package no.fint.consumer.controller;

import no.fint.model.relation.FintResource;
import no.fint.pwfa.model.Owner;
import no.fint.relations.FintResourceAssembler;
import no.fint.relations.FintResourceSupport;
import org.springframework.stereotype.Component;

@Component
public class OwnerAssembler extends FintResourceAssembler<Owner> {

    public OwnerAssembler() {
        super(OwnerController.class);
    }

    @Override
    public FintResourceSupport assemble(Owner owner, FintResource<Owner> fintResource) {
        return createResourceWithId(owner.getId(), fintResource);
    }
}
