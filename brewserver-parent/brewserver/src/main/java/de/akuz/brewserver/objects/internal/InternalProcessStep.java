package de.akuz.brewserver.objects.internal;

import de.akuz.brewcontroller.IProcessStep;
import de.akuz.brewserver.hardware.BrewHardwareInterface;
import de.akuz.brewserver.hardware.BrewHardwareInterface.BrewHardwareListener;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.ProcessStep.StepType;

public class InternalProcessStep implements IProcessStep, BrewHardwareListener {

	private ProcessStep processStep;

	/**
	 * MashStep
	 */
	private long timeTempReached = -1;
	private float targetTemp;
	private long stepTime;
	private long timeLeft;
	private String name;
	private boolean mashStepStarted;

	private BrewHardwareInterface hardware;

	/**
	 * NotificationStep
	 */
	private String message;
	private boolean acknowledged;

	public InternalProcessStep(ProcessStep receivedStep) {
		this.processStep = receivedStep;
		if (receivedStep.getStepType() == StepType.MASH) {
			createMashStep(receivedStep);
		} else if (receivedStep.getStepType() == StepType.NOTIFICATION) {
			createNotificationStep(receivedStep);
		}
	}

	private void createNotificationStep(ProcessStep step) {
		message = step.getMessage();
	}

	private void createMashStep(ProcessStep step) {
		targetTemp = step.getTargetTemp();
		stepTime = step.getStepTime();
		name = step.getName();
	}

	public void start() {
		if (processStep.getStepType() == StepType.MASH) {
			hardware.setTargetTemperature(targetTemp);
		}
	}

	private boolean hasNotificationEnded() {
		return acknowledged;
	}

	private boolean hasMashEnded() {
		if (timeTempReached == -1) {
			return false;
		}
		long timeLeft = (timeTempReached + stepTime)
				- System.currentTimeMillis();
		if (timeLeft < 0) {
			timeLeft = 0;
		}
		this.timeLeft = timeLeft;
		return timeLeft == 0;
	}

	public void notifyCurrentTemp(float temp) {
		if (temp >= targetTemp && timeTempReached == -1) {
			mashStepStarted = true;
			timeTempReached = System.currentTimeMillis();
		}
	}

	public void acknowledge() {
		acknowledged = true;
	}

	public long getTimeTempReached() {
		return timeTempReached;
	}

	public void setTimeTempReached(long timeTempReached) {
		this.timeTempReached = timeTempReached;
	}

	public float getTargetTemp() {
		return targetTemp;
	}

	public void setTargetTemp(float targetTemp) {
		this.targetTemp = targetTemp;
	}

	public long getStepTime() {
		return stepTime;
	}

	public void setStepTime(long stepTime) {
		this.stepTime = stepTime;
	}

	public long getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public ProcessStep getProcessStep() {
		return processStep;
	}

	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	public boolean isMashStepStarted() {
		return mashStepStarted;
	}

	public void setMashStepStarted(boolean mashStepStarted) {
		this.mashStepStarted = mashStepStarted;
	}

	public void setHardware(BrewHardwareInterface hardware) {
		this.hardware = hardware;
		this.hardware.registerListener(this);
	}

	public boolean isFinished() {
		if (processStep.getStepType() == StepType.MASH) {
			return hasMashEnded();
		}
		if (processStep.getStepType() == StepType.NOTIFICATION) {
			return hasNotificationEnded();
		}
		throw new IllegalStateException("Unkwown step type");
	}

	public ProcessStep getConfiguredProcessStep() {
		return processStep;
	}

	public void measuredTempChanged(float temp) {
		notifyCurrentTemp(temp);
	}

	public void relayStateChanged(int relayNumber, boolean state) {
		// TODO Auto-generated method stub

	}

	public void error(Exception e) {
		// TODO Auto-generated method stub

	}

}
