package de.akuz.brewserver.objects;

public class State {

	public static enum MODE {
		MASHING, COOKING, FIXED_TEMP, OFF;
	}

	private long timeRunning;
	private MODE operationMode;
	private float currentTemp;
	private boolean stepStarted;
	private long timeLeft;

	private int currentStepNumber;
	private int totalSteps;

	private ProcessStep currentStep;

	public float getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(float currentTemp) {
		this.currentTemp = currentTemp;
	}

	public int getCurrentStepNumber() {
		return currentStepNumber;
	}

	public void setCurrentStepNumber(int currentStepNumber) {
		this.currentStepNumber = currentStepNumber;
	}

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	public ProcessStep getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(ProcessStep currentStep) {
		this.currentStep = currentStep;
	}

	public boolean isStepStarted() {
		return stepStarted;
	}

	public void setStepStarted(boolean stepStarted) {
		this.stepStarted = stepStarted;
	}

	public long getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}

	public long getTimeRunning() {
		return timeRunning;
	}

	public void setTimeRunning(long timeRunning) {
		this.timeRunning = timeRunning;
	}

	public MODE getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(MODE operationMode) {
		this.operationMode = operationMode;
	}

}
