package de.akuz.brewserver.mock;

import de.akuz.brewserver.hardware.AbstractHardwareImpl;

public class TestHardware extends AbstractHardwareImpl {

	private class MockThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				if (currentTemp < targetTemp) {
					currentTemp = currentTemp + 1.0f;
					notifyMeasuredTempChanged(currentTemp);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private float targetTemp;

	private float currentTemp = 13.0f;

	private MockThread thread = new MockThread();

	public TestHardware() {
		thread.start();
	}

	public void setTargetTemperature(float temp) {
		targetTemp = temp;

	}

	public float getCurrentTemperature() {
		return currentTemp;
	}

	public void heatingOff() {
		currentTemp = 17;
		targetTemp = 0;

	}

	public void setOptions(String options) {
		// TODO Auto-generated method stub

	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void cook() {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
