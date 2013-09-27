package de.akuz.brewserver.resources;

import java.security.InvalidParameterException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.util.PojoConverter;

@Path("/state/{type}")
@Produces(MediaType.APPLICATION_JSON)
public class FullStateResource extends AbstractBrewServerResource {

	private final static Logger log = LoggerFactory
			.getLogger(FullStateResource.class);

	@GET
	public State getState(@PathParam("type") String type) {
		if (StringUtils.isEmpty(type) || "small".equals(type)) {
			log.debug("Returning small state update");
			return PojoConverter.convertFromBrewControllerState(brewController
					.getCurrentState());
		} else if ("full".equals(type)) {
			log.debug("Returning full state update");
			return getFullState();
		}
		log.error("Unkown path parameter for state type: " + type);
		throw new InvalidParameterException("Unkwon parameter " + type);
	}

}
