package de.akuz.brewserver.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.akuz.brewcontroller.BrewControllerException;
import de.akuz.brewserver.objects.State;

@Path("/private/stop")
@Produces(MediaType.APPLICATION_JSON)
public class BrewServerStopResource extends AbstractBrewServerResource {

	@GET
	public State stop() throws BrewControllerException {
		brewController.stopMashing();
		return convertFromBrewControllerState(brewController.getCurrentState());
	}

}
