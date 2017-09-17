package no.fint.consumer.controller;

import no.fint.model.relation.FintResource;
import no.fint.pwfa.model.Dog;
import no.fint.relations.FintResourceAssembler;
import no.fint.relations.FintResourceSupport;
import org.springframework.stereotype.Component;

@Component
public class DogAssembler extends FintResourceAssembler<Dog> {
    public DogAssembler() {
        super(DogController.class);
    }

    @Override
    public FintResourceSupport assemble(Dog dog, FintResource<Dog> fintResource) {
        return createResourceWithId(dog.getId(), fintResource);
    }
}
