package de.akuz.brewcontroller;

import de.akuz.brewserver.hardware.BrewHardwareInterface;
import de.akuz.brewserver.objects.BrewControllerConfiguration;

public interface IBrewController {

	public static interface BrewControllerListener {
		public void measuredTempChanged(float measuredTemp);

		public void error(Exception e);

		public void stepChanged(IProcessStep step);
	}

	public void setMashConfiguration(BrewControllerConfiguration config)
			throws BrewControllerException;

	public void startMashing() throws BrewControllerException;

	public void stopMashing() throws BrewControllerException;

	public void setFixedTemperature(float temp) throws BrewControllerException;

	public void acknowledgeNotificationStep() throws BrewControllerException;

	public void registerBrewControllerListener(BrewControllerListener listener);

	public void unregisterBrewControllerListener(BrewControllerListener listener);

	public BrewControllerState getCurrentState();

	public boolean isMashing();

	public void setHardware(BrewHardwareInterface hardware);

	public void setSerialiaztionPath(String path);

}
