package de.akuz.brewcontroller;

import java.util.ArrayList;
import java.util.List;

import de.akuz.brewserver.objects.FullState;
import de.akuz.brewserver.objects.FullState.TempPoint;

public class TemperatureLog {

	private List<TempPoint> tempPoints = new ArrayList<FullState.TempPoint>();

	public void addTempPoint(long timeRunning, float temp) {

		long lastTime = 0;
		if (tempPoints.size() > 0) {
			lastTime = tempPoints.get(tempPoints.size() - 1).time;
		}
		if ((System.currentTimeMillis() - lastTime) > 10000) {
			TempPoint p = new TempPoint();
			p.time = timeRunning;
			p.temp = temp;
			tempPoints.add(p);
		}
	}

	public List<TempPoint> getTempPoints() {
		return tempPoints;
	}

	public void setTempPoints(List<TempPoint> tempPoints) {
		this.tempPoints = tempPoints;
	}

}
