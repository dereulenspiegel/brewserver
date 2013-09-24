package de.akuz.brewserver.hardware;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHardwareImpl implements BrewHardwareInterface {

	private List<BrewHardwareListener> listeners = new ArrayList<BrewHardwareInterface.BrewHardwareListener>();

	public void registerListener(BrewHardwareListener listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}

	}

	public void unregisterListener(BrewHardwareListener listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}

	}

	protected void notifyMeasuredTempChanged(float temp) {
		synchronized (listeners) {
			for (BrewHardwareListener l : listeners) {
				l.measuredTempChanged(temp);
			}
		}
	}

	protected void notifyError(Exception e) {
		synchronized (listeners) {
			for (BrewHardwareListener l : listeners) {
				l.error(e);
			}
		}
	}

}
