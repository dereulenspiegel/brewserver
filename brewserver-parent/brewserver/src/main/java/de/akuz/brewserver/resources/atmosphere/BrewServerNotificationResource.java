package de.akuz.brewserver.resources.atmosphere;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.SuspendResponse;

import de.akuz.brewcontroller.BrewControllerException;
import de.akuz.brewserver.objects.Notification;
import de.akuz.brewserver.resources.AbstractBrewServerResource;

@Path("/notify")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrewServerNotificationResource extends AbstractBrewServerResource {

	private Broadcaster broadcaster;

	public BrewServerNotificationResource() {
		broadcaster = BroadcasterFactory.getDefault().lookup("/notify", true);
	}

	@GET
	public SuspendResponse<Notification> subscribe() {
		SuspendResponse<Notification> suspendedResponse;
		suspendedResponse = new SuspendResponse.SuspendResponseBuilder<Notification>()
				.broadcaster(broadcaster).outputComments(true).build();
		return suspendedResponse;
	}

	@POST
	public void ackNotification(Notification notif)
			throws BrewControllerException {
		if (notif != null) {
			brewController.acknowledgeNotificationStep();
		}
	}

}
