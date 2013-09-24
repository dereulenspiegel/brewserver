package de.akuz.brewserver.resources.atmosphere;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.SuspendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.resources.AbstractBrewServerResource;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BrewServerStatusResource extends AbstractBrewServerResource {

	private Logger logger = LoggerFactory
			.getLogger(BrewServerStatusResource.class);

	private Broadcaster broadcaster;

	public BrewServerStatusResource() {
		broadcaster = BroadcasterFactory.getDefault().lookup("/state", true);
	}

	@GET
	public SuspendResponse<State> state() {
		logger.debug("GET on /state/, returning suspended answer for state resource");
		// State state = getCurrentState();
		SuspendResponse<State> suspendedResponse;
		suspendedResponse = new SuspendResponse.SuspendResponseBuilder<State>()
				.broadcaster(broadcaster).outputComments(true).build();
		return suspendedResponse;
	}

	private State getCurrentState() {
		State state = convertFromBrewControllerState(brewController
				.getCurrentState());
		return state;
	}

}
