package de.akuz.brewserver.resources.atmosphere;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.atmosphere.annotation.Suspend;

import de.akuz.brewcontroller.BrewControllerException;
import de.akuz.brewserver.objects.Notification;
import de.akuz.brewserver.resources.AbstractBrewServerResource;

@Path("/notification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrewServerNotificationResource extends AbstractBrewServerResource {

	@Suspend
	@GET
	public Notification broadcastNotification() {
		return null;
	}

	@POST
	public void ackNotification(Notification notif)
			throws BrewControllerException {
		if (notif != null) {
			brewController.acknowledgeNotificationStep();
		}
	}

}
