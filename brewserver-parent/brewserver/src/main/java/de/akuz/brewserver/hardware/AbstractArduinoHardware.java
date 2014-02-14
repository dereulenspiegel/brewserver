package de.akuz.brewserver.hardware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public abstract class AbstractArduinoHardware extends AbstractHardwareImpl
		implements SerialPortEventListener {

	private Logger log = LoggerFactory.getLogger(AbstractArduinoHardware.class);

	private SerialPort serialPort;

	private InputStream is;
	private OutputStream os;
	private BufferedReader br;
	private BufferedWriter bw;
	
	protected void openPort(String portName) {
		try {
			log.debug("Initializing serial connection");
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			CommPort commPort = portIdentifier.open(this.getClass()
					.getSimpleName(), 2000);

			if ((commPort instanceof SerialPort)) {
				log.debug("Opening COM port " + portName);
				this.serialPort = (SerialPort) commPort;
				this.serialPort.setSerialPortParams(9600,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				is = this.serialPort.getInputStream();
				os = this.serialPort.getOutputStream();
				br = new BufferedReader(new InputStreamReader(is));
				bw = new BufferedWriter(new OutputStreamWriter(os));

				serialPort.notifyOnDataAvailable(true);
				serialPort.addEventListener(this);
				try {
					log.debug("Waiting for Arduino");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.warn(
							"Exception while waiting till the device is ready",
							e);
				}
				heatingOff();
			} else {
				log.error("Specified port " + portName
						+ " is not a serial port");
			}
		} catch (NoSuchPortException e) {
			log.error("SerialPort does not exist", e);
			notifyError(e);
			throw new IllegalArgumentException(e);
		} catch (PortInUseException e) {
			log.error("SerialPort is in use", e);
			notifyError(e);
			throw new IllegalArgumentException(e);
		} catch (UnsupportedCommOperationException e) {
			log.error("Can't configure SerialPort", e);
			notifyError(e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			log.error("Can't open In- or Outputstream", e);
			notifyError(e);
			throw new IllegalArgumentException(e);
		} catch (TooManyListenersException e) {
			log.error("This SerialPort has already a listener", e);
			notifyError(e);
			throw new IllegalArgumentException(e);
		}
	}

	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				log.debug("Received serial event DATA_AVAILABLE");
				dataAvailable(br);
			} catch (Exception e) {
				notifyError(e);
			}
		}

	}

	protected abstract void dataAvailable(BufferedReader reader);

	protected void write(String data) throws IOException {
		bw.write(data);
		bw.flush();
	}

	protected void write(char[] data) throws IOException {
		bw.write(data);
		bw.flush();
	}

	public void close() {
		try {
			log.debug("Closing serial connection");
			heatingOff();
			bw.flush();
			bw.close();
			br.close();
			serialPort.close();
		} catch (IOException e) {
			log.error("Error while closing SerialPort", e);
			notifyError(e);
		}

	}

}
