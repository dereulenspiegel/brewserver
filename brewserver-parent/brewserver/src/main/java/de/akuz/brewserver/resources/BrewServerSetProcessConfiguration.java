package de.akuz.brewserver.resources;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.akuz.brewserver.objects.BrewControllerConfiguration;

@Path(AbstractBrewServerResource.PRIVATE_PATH + "config/set")
@Produces(MediaType.APPLICATION_JSON)
public class BrewServerSetProcessConfiguration extends
		AbstractBrewServerResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void setConfiguration(@Valid BrewControllerConfiguration config)
			throws Exception {
		brewController.setMashConfiguration(config);
	}

}
