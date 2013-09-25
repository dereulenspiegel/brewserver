package de.akuz.brewserver.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import de.akuz.brewcontroller.BrewControllerException;

@Path(AbstractBrewServerResource.PRIVATE_PATH + "/cook/{state}")
public class CookingResource extends AbstractBrewServerResource {

	@GET
	public void getTemp(@PathParam("state") String state)
			throws BrewControllerException {
		if ("start".equals(state)) {
			brewController.startCooking();
		} else if ("stop".equals(state)) {
			brewController.stopCooking();
		} else {
			throw new BrewControllerException("Unbekanntes Kommando: " + state);
		}
	}

}
