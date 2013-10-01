package de.akuz.brewserver.resources;

import java.util.ArrayList;
import java.util.List;

import de.akuz.brewcontroller.BrewController;
import de.akuz.brewcontroller.BrewControllerState;
import de.akuz.brewcontroller.IBrewController;
import de.akuz.brewserver.objects.FullState;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.objects.internal.InternalProcessStep;
import de.akuz.brewserver.util.PojoConverter;

public abstract class AbstractBrewServerResource {

	public static final String PRIVATE_PATH = "/private/";

	protected IBrewController brewController = BrewController.getInstance();

	protected InternalProcessStep convertFromReceivedStep(ProcessStep step) {
		return PojoConverter.convertFromReceivedStep(step);
	}

	protected State convertFromBrewControllerState(BrewControllerState brewState) {
		return PojoConverter.convertFromBrewControllerState(brewState);
	}

	protected FullState getFullState() {
		BrewControllerState controllerState = brewController.getCurrentState();
		FullState state = new FullState();
		state.setCurrentStep(controllerState.getCurrentStep() != null ? controllerState
				.getCurrentStep().getProcessStep() : null);
		state.setCurrentStepNumber(controllerState.getCurrentStepNumber());
		state.setCurrentTemp(controllerState.getLastTemp());
		state.setOperationMode(controllerState.getOperationMode());
		state.setName(controllerState.getName());
		List<ProcessStep> processSteps = new ArrayList<ProcessStep>(
				controllerState.getProcessSteps().size());
		for (InternalProcessStep internalStep : controllerState
				.getProcessSteps()) {
			processSteps.add(internalStep.getProcessStep());
		}
		state.setProcessSteps(processSteps);
		state.setTimeRunning(controllerState.getTimeRunning());

		state.setTempPoints(controllerState.getTempLog().getTempPoints());
		state.setTotalSteps(controllerState.getProcessSteps().size());
		return state;
	}
}
