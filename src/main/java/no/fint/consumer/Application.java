package no.fint.consumer;

import com.github.springfox.loader.EnableSpringfox;
import no.fint.audit.EnableFintAudit;
import no.fint.events.annotations.EnableFintEvents;
import no.fint.events.controller.FintEventsController;
import no.fint.relations.annotations.EnableFintRelations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFintRelations
@EnableFintEvents
@EnableFintAudit
@EnableScheduling
@EnableSpringfox(includeControllers = {FintEventsController.class})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
