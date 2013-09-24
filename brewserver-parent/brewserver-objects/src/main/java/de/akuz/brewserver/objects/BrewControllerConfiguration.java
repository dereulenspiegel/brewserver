package de.akuz.brewserver.objects;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BrewControllerConfiguration {

	private String mashName;
	@NotNull
	@Size(min = 1)
	private List<ProcessStep> processSteps;

	public String getMashName() {
		return mashName;
	}

	public void setMashName(String mashName) {
		this.mashName = mashName;
	}

	public List<ProcessStep> getProcessSteps() {
		return processSteps;
	}

	public void setProcessSteps(List<ProcessStep> processSteps) {
		this.processSteps = processSteps;
	}

	public void addProcessStep(ProcessStep step) {
		if (processSteps == null) {
			processSteps = new ArrayList<ProcessStep>();
		}
		processSteps.add(step);
	}

}
