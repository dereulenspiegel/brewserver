package de.akuz.brewserver;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import de.akuz.brewcontroller.BrewController;
import de.akuz.brewcontroller.IProcessStep;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.util.PojoConverter;

public class BrewServerAtmosphereListener implements de.akuz.brewcontroller.IBrewController.BrewControllerListener {

	private Broadcaster broadcaster;
	private BrewController brewController = BrewController.getInstance();

	public BrewServerAtmosphereListener() {
		broadcaster = BroadcasterFactory.getDefault().lookup("/state", true);
		broadcaster.scheduleFixedBroadcast(new Callable<State>() {

			public State call() throws Exception {
				State state = PojoConverter
						.convertFromBrewControllerState(BrewController
								.getInstance().getCurrentState());
				return state;
			}
		}, 30, TimeUnit.SECONDS);
	}

	public BrewServerAtmosphereListener(Broadcaster broadcaster) {
		// this.broadcaster = broadcaster;
	}

	public void measuredTempChanged(float measuredTemp) {
		broadcaster.broadcast(getCurrentBrewControllerState());
	}

	public void error(Exception e) {
		// TODO Auto-generated method stub

	}

	public void stepChanged(ProcessStep step) {
		broadcaster.broadcast(getCurrentBrewControllerState());

	}

	private State getCurrentBrewControllerState() {
		return PojoConverter.convertFromBrewControllerState(brewController
				.getCurrentState());
	}

	public void stepChanged(IProcessStep step) {
		// TODO Auto-generated method stub
		
	}

}
