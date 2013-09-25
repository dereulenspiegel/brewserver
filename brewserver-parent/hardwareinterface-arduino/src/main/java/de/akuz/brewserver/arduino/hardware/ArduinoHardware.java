package de.akuz.brewserver.arduino.hardware;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akuz.brewserver.hardware.AbstractHardwareImpl;

public class ArduinoHardware extends AbstractHardwareImpl implements
		SerialPortEventListener {

	private static enum ArduinoCommand {
		HEATING_OFF("O"), COOK("C"), TEMPERATURE("T"), PID_PARAMS("P");

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

	private SerialPort serialPort;
	private String portName;
	private String pidParams;

	private InputStream is;
	private OutputStream os;
	private BufferedReader br;
	private BufferedWriter bw;

	private float lastTemp;

	public void setTargetTemperature(float temp) {
		String tempString = tempNumberFormat.format(temp);
		writeCommand(ArduinoCommand.TEMPERATURE.getCommand() + tempString);

	}

	public void heatingOff() {
		writeCommand(ArduinoCommand.HEATING_OFF.getCommand());
	}

	public float getCurrentTemperature() {
		return lastTemp;
	}

	/*
	 * OptionsString port=/dev/tty0|pid=100;100;100
	 * 
	 * @see
	 * de.akuz.brewserver.hardware.BrewHardwareInterface#setOptions(java.lang
	 * .String)
	 */
	public void setOptions(String options) {
		String[] parts = options.split("|");
		for (String s : parts) {
			String[] keyValue = s.split("=");
			if ("port".equals(keyValue[0])) {
				portName = keyValue[1];
			}
			if ("pid".equals(keyValue[0])) {
				pidParams = keyValue[1];
			}
		}

	}

	public void init() {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			CommPort commPort = portIdentifier.open(this.getClass()
					.getSimpleName(), 2000);

			if ((commPort instanceof SerialPort)) {
				this.serialPort = (SerialPort) commPort;
				this.serialPort.setSerialPortParams(9600,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_EVEN);

				is = this.serialPort.getInputStream();
				os = this.serialPort.getOutputStream();
				br = new BufferedReader(new InputStreamReader(is));
				bw = new BufferedWriter(new OutputStreamWriter(os));

				serialPort.notifyOnDataAvailable(true);
				serialPort.addEventListener(this);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.warn(
							"Exception while waiting till the device is ready",
							e);
				}
				writeCommand(ArduinoCommand.PID_PARAMS.getCommand() + pidParams);
			}
		} catch (NoSuchPortException e) {
			log.error("SerialPort does not exist", e);
			notifyError(e);
		} catch (PortInUseException e) {
			log.error("SerialPort is in use", e);
			notifyError(e);
		} catch (UnsupportedCommOperationException e) {
			log.error("Can't configure SerialPort", e);
			notifyError(e);
		} catch (IOException e) {
			log.error("Can't open In- or Outputstream", e);
			notifyError(e);
		} catch (TooManyListenersException e) {
			log.error("This SerialPort has already a listener", e);
			notifyError(e);
		}

	}

	public void cook() {
		writeCommand(ArduinoCommand.COOK.getCommand());

	}

	public void close() {
		try {
			bw.flush();
			bw.close();
			br.close();
			serialPort.close();
		} catch (IOException e) {
			log.error("Error while closing SerialPort", e);
			notifyError(e);
		}

	}

	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String data = br.readLine();
				if (data.startsWith("T")) {
					lastTemp = parseLine(data);
				} else {
					log.warn("Received unexpected line from controller: "
							+ data);
				}
				notifyMeasuredTempChanged(lastTemp);
			} catch (IOException e) {
				notifyError(e);
			}
		}

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
			bw.write(command + TERMINATION_CHAR);
			bw.flush();
		} catch (IOException e) {
			notifyError(e);
		}
	}

}
