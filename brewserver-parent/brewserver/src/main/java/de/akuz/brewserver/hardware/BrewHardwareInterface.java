package de.akuz.brewserver.hardware;

public interface BrewHardwareInterface {

	public static interface BrewHardwareListener {
		public void measuredTempChanged(float temp);

		public void error(Exception e);
	}

	public void setTargetTemperature(float temp);

	public void heatingOff();

	public float getCurrentTemperature();

	public void setOptions(String options);

	public void init();

	public void registerListener(BrewHardwareListener listener);

	public void unregisterListener(BrewHardwareListener listener);

	public void cook();

	public void close();

}
