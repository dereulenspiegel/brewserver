package de.akuz.brewserver.objects;

public class ProcessStep {

	public static enum StepType {
		MASH, NOTIFICATION;
	}

	private int[] switchedOnRelays;
	private StepType stepType;

	/**
	 * MashStep
	 */
	private long timeTempReached = -1;
	private float targetTemp;
	private long stepTime;
	private String name;

	/**
	 * NotificationStep
	 */
	private String message;
	private boolean acknowledged;

	public int[] getSwitchedOnRelays() {
		return switchedOnRelays;
	}

	public void setSwitchedOnRelays(int[] switchedOnRelays) {
		this.switchedOnRelays = switchedOnRelays;
	}

	public StepType getStepType() {
		return stepType;
	}

	public void setStepType(StepType stepType) {
		this.stepType = stepType;
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

}
