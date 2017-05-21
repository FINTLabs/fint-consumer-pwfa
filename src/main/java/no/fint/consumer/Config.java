package no.fint.consumer;


import com.google.common.collect.ImmutableMap;
import no.fint.consumer.utils.RestEndpoints;
import no.fint.pwfa.model.Dog;
import no.fint.pwfa.model.Owner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {

    @Value("${server.contextPath:}")
    private String contextPath;

    @Qualifier("linkMapper")
    @Bean
    public Map<String, String> linkMapper() {
        return ImmutableMap.of(
                Dog.class.getName(), fullPath(RestEndpoints.DOG),
                Owner.class.getName(), fullPath(RestEndpoints.OWNER)
        );
    }

    String fullPath(String path) {
        return String.format("%s%s", contextPath, path);
    }

}
