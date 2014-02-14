package de.akuz.brewserver.arduino.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akuz.brewserver.hardware.AbstractArduinoHardware;

public class ArduinoHardware extends AbstractArduinoHardware {

	private static enum ArduinoCommand {
		HEATING_OFF("O"), COOK("C"), TEMPERATURE("T");

		private String command;

		private ArduinoCommand(String command) {
			this.command = command;
		}

		public String getCommand() {
			return command;
		}
	}

	private final static Logger log = LoggerFactory
			.getLogger(ArduinoHardware.class);

	private final static NumberFormat tempNumberFormat = NumberFormat
			.getNumberInstance();
	static {
		tempNumberFormat.setMaximumFractionDigits(2);
	}

	private final static String TERMINATION_CHAR = "\n";

	private String portName;

	private float lastTemp;

	public void setTargetTemperature(float temp) {
		int intTemp = Math.round(temp);
		String tempString = String.valueOf(intTemp);
		writeCommand(ArduinoCommand.TEMPERATURE.getCommand() + tempString);

	}

	public void heatingOff() {
		writeCommand(ArduinoCommand.HEATING_OFF.getCommand());
	}

	public float getCurrentTemperature() {
		return lastTemp;
	}

	/*
	 * OptionsString port=/dev/tty0;
	 * 
	 * @see
	 * de.akuz.brewserver.hardware.BrewHardwareInterface#setOptions(java.lang
	 * .String)
	 */
	public void setOptions(String options) {
		String[] parts = options.split("\\|");
		for (String s : parts) {
			String[] keyValue = s.split("=");
			if (keyValue.length == 2) {
				if ("port".equals(keyValue[0])) {
					String value = keyValue[1];
					value.replaceAll(";", "");
					portName = value;
				}
			} else {
				log.warn("Invalid configuration value: " + s);
			}
		}

	}

	public void init() {
		openPort(portName);
	}

	public void cook() {
		log.debug("Starting cooking");
		writeCommand(ArduinoCommand.COOK.getCommand());

	}

	private float parseLine(String data) {
		if (data.startsWith("T")) {
			String tempString = data.substring(1);
			return Float.parseFloat(tempString);
		}
		return -1.0f;
	}

	private void writeCommand(String command) {
		try {
			log.debug("Sending command to Arduino " + command);
			write(command + TERMINATION_CHAR);
		} catch (IOException e) {
			notifyError(e);
		}
	}

	@Override
	protected void dataAvailable(BufferedReader reader) {
		try {
			String data = reader.readLine();
			parseLine(data);
		} catch (IOException e) {
			log.error("Error while reading line", e);
			notifyError(e);
		}

	}

}
