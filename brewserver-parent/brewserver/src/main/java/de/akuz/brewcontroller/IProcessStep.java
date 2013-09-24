package de.akuz.brewcontroller;

import de.akuz.brewserver.hardware.BrewHardwareInterface;
import de.akuz.brewserver.objects.ProcessStep;

public interface IProcessStep {

	public void setHardware(BrewHardwareInterface hardware);

	public boolean isFinished();

	public ProcessStep getConfiguredProcessStep();

}
