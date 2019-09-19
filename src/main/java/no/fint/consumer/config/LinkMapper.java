package no.fint.consumer.config;

import no.fint.consumer.utils.RestEndpoints;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

import no.fint.model.pwfa.pwfa.*;
import no.fint.model.felles.*;

public class LinkMapper {

	public static Map<String, String> linkMapper(String contextPath) {
		return ImmutableMap.<String,String>builder()
			.put(Dog.class.getName(), contextPath + RestEndpoints.DOG)
			.put(Owner.class.getName(), contextPath + RestEndpoints.OWNER)
			/* .put(TODO,TODO) */
			.build();
	}

}
