package de.akuz.brewserver.resources;

import java.security.InvalidParameterException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.util.PojoConverter;

@Path("/state/{type}")
@Produces(MediaType.APPLICATION_JSON)
public class FullStateResource extends AbstractBrewServerResource {

	@GET
	public State getState(@PathParam("size") String type) {
		if (StringUtils.isEmpty(type) || "small".equals(type)) {
			return PojoConverter.convertFromBrewControllerState(brewController
					.getCurrentState());
		} else if ("full".equals(type)) {
			return getFullState();
		}
		throw new InvalidParameterException("Unkwon parameter " + type);
	}

}
