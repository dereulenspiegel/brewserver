package de.akuz.brewserver.objects;

import java.util.List;

public class FullState extends State {

	public static class TempPoint {
		public long time;
		public float temp;
	}

	private String name;
	private List<ProcessStep> processSteps;
	private List<TempPoint> tempPoints;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProcessStep> getProcessSteps() {
		return processSteps;
	}

	public void setProcessSteps(List<ProcessStep> processSteps) {
		this.processSteps = processSteps;
	}

	public List<TempPoint> getTempPoints() {
		return tempPoints;
	}

	public void setTempPoints(List<TempPoint> tempPoints) {
		this.tempPoints = tempPoints;
	}

}
