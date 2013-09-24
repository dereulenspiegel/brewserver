package de.akuz.brewserver.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.akuz.brewserver.objects.SetTemp;

@Path(AbstractBrewServerResource.PRIVATE_PATH + "/temp")
public class BrewServerSetTempResource extends AbstractBrewServerResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void setTemp(SetTemp setTemp) throws Exception {
		brewController.setFixedTemperature(setTemp.getTargetTemp());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SetTemp getTemp() {
		SetTemp temp = new SetTemp();
		temp.setTargetTemp(brewController.getCurrentState().getLastTemp());
		return temp;
	}

}
