package de.akuz.brewserver.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.akuz.brewserver.objects.State;

@Path("/private/start")
@Produces(MediaType.APPLICATION_JSON)
public class BrewServerStartResource extends AbstractBrewServerResource {

	@GET
	public State startMash() throws Exception {
		brewController.startMashing();
		return getFullState();
	}

}
