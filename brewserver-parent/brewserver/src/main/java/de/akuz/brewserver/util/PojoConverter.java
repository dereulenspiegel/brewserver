package de.akuz.brewserver.util;

import javax.ws.rs.WebApplicationException;

import org.eclipse.jetty.http.HttpStatus;

import de.akuz.brewcontroller.BrewControllerState;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.ProcessStep.StepType;
import de.akuz.brewserver.objects.State;
import de.akuz.brewserver.objects.internal.InternalProcessStep;

public class PojoConverter {

	public static State convertFromBrewControllerState(
			BrewControllerState brewState) {
		State state = new State();
		state.setCurrentStepNumber(brewState.getCurrentStepNumber());
		state.setCurrentTemp(brewState.getLastTemp());
		state.setTotalSteps(brewState.getProcessSteps().size());
		state.setTimeRunning(System.currentTimeMillis()
				- brewState.getTimeStarted());
		state.setOperationMode(brewState.getOperationMode());
		if (brewState.getCurrentStep() != null) {
			state.setStepStarted(brewState.getCurrentStep().isMashStepStarted());
			state.setTimeLeft(brewState.getCurrentStep().getTimeLeft());
			state.setCurrentStep(brewState.getCurrentStep().getProcessStep());
		}
		return state;
	}

	public static InternalProcessStep convertFromReceivedStep(ProcessStep step) {
		InternalProcessStep internalStep = null;
		if (step.getStepType() == StepType.MASH) {
			internalStep = new InternalProcessStep(step);
		} else if (step.getStepType() == StepType.NOTIFICATION) {
			internalStep = new InternalProcessStep(step);
		} else {
			WebApplicationException exc = new WebApplicationException(
					HttpStatus.EXPECTATION_FAILED_417);
			throw exc;
		}

		return internalStep;
	}

}
