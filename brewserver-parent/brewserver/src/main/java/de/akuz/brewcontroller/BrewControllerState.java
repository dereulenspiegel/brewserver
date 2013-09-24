package de.akuz.brewcontroller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.akuz.brewserver.objects.internal.InternalProcessStep;

public class BrewControllerState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7036681242395711428L;

	private String name;

	private int currentStepNumber = -1;
	private List<InternalProcessStep> processSteps = new ArrayList<InternalProcessStep>();
	private float lastTemp;
	private boolean running;

	private long timeStarted;

	private TemperatureLog tempLog = new TemperatureLog();

	public int getCurrentStepNumber() {
		return currentStepNumber;
	}

	public void setCurrentStepNumber(int currentStepNumber) {
		this.currentStepNumber = currentStepNumber;
	}

	public List<InternalProcessStep> getProcessSteps() {
		return processSteps;
	}

	public void setProcessSteps(List<InternalProcessStep> processSteps) {
		this.processSteps = processSteps;
	}

	public void addProcessStep(InternalProcessStep step) {
		if (processSteps == null) {
			processSteps = new ArrayList<InternalProcessStep>();
		}
		processSteps.add(step);
	}

	public void addProcessStep(InternalProcessStep step, int position) {
		if (processSteps == null) {
			processSteps = new ArrayList<InternalProcessStep>();
		}
		processSteps.add(position, step);

	}

	public void removeProcessStep(InternalProcessStep step) {
		if (processSteps != null) {
			processSteps.remove(step);
		}
	}

	public void removeProcessStep(int position) {
		if (processSteps != null) {
			processSteps.remove(position);
		}
	}

	@JsonIgnore
	public InternalProcessStep getCurrentStep() {
		if (currentStepNumber >= processSteps.size() || currentStepNumber < 0) {
			return null;
		}
		return processSteps.get(currentStepNumber);
	}

	public float getLastTemp() {
		return lastTemp;
	}

	public void setLastTemp(float lastTemp) {
		this.lastTemp = lastTemp;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		if (running) {
			timeStarted = System.currentTimeMillis();
		}
		this.running = running;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
	}

	public TemperatureLog getTempLog() {
		return tempLog;
	}

	public void setTempLog(TemperatureLog tempLog) {
		this.tempLog = tempLog;
	}

	@JsonIgnore
	public long getTimeRunning() {
		return System.currentTimeMillis() - timeStarted;
	}

}
