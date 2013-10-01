package de.akuz.brewcontroller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.akuz.brewserver.hardware.BrewHardwareInterface;
import de.akuz.brewserver.hardware.BrewHardwareInterface.BrewHardwareListener;
import de.akuz.brewserver.objects.BrewControllerConfiguration;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.ProcessStep.StepType;
import de.akuz.brewserver.objects.State.MODE;
import de.akuz.brewserver.objects.internal.InternalProcessStep;

public class BrewController implements IBrewController, BrewHardwareListener {

	private final static Logger log = LoggerFactory
			.getLogger(BrewController.class);

	private List<BrewControllerListener> listeners = new ArrayList<BrewControllerListener>();

	private static BrewController instance = new BrewController();

	private BrewHardwareInterface hardware;

	private BrewControllerState state = new BrewControllerState();

	private InternalProcessStep currentStep;

	private ProcessThread processThread;

	private ObjectMapper jacksonMapper = new ObjectMapper();
	private String serializationPath;

	private long lastStateSerialization = 0;
	private final static long serializationInterval = 60000;

	public static BrewController getInstance() {
		return instance;
	}

	private BrewController() {

	}

	public void setSerialiaztionPath(String path) {
		this.serializationPath = path;
	}

	public void startMashing() throws BrewControllerException {
		checkNotRunning();
		checkStartParameters();
		hardware.heatingOff();
		state.setRunning(MODE.MASHING);
		state.setTimeStarted(System.currentTimeMillis());
		processThread = new ProcessThread();
		processThread.start();
	}

	private void checkStartParameters() throws BrewControllerException {
		if (StringUtils.isEmpty(serializationPath)) {
			throw new BrewControllerStateException(
					"Path to serialize state to is not set");
		}
		if (state.getProcessSteps() == null
				|| state.getProcessSteps().isEmpty()) {
			throw new BrewControllerStateException(
					"No Process steps are defined");
		}
	}

	public void stopMashing() {
		resetBrewController();
	}

	private void resetBrewController() {
		state.setRunning(MODE.OFF);
		if (processThread != null) {
			processThread.interrupt();
		}
		processThread = null;
		hardware.heatingOff();
		state = new BrewControllerState();
	}

	private InternalProcessStep getNextMashStep() {
		if (state.getCurrentStepNumber() < state.getProcessSteps().size() - 1) {
			state.setCurrentStepNumber(state.getCurrentStepNumber() + 1);
			currentStep = state.getProcessSteps().get(
					state.getCurrentStepNumber());
			return currentStep;
		}
		return null;
	}

	public List<InternalProcessStep> getMashSteps() {
		return Collections.unmodifiableList(state.getProcessSteps());
	}

	public void measuredTempChanged(float temp) {
		state.setLastTemp(temp);
		if (state.getOperationMode() == MODE.MASHING) {
			state.getTempLog().addTempPoint(
					System.currentTimeMillis() - state.getTimeStarted(), temp);
		}
		notifyMeasuredTemp(temp);
	}

	public void setConfiguration(BrewControllerConfiguration config)
			throws BrewControllerException {

	}

	public void relayStateChanged(int relayNumber, boolean state) {
		// TODO Auto-generated method stub

	}

	public void error(Exception e) {
		// TODO Auto-generated method stub

	}

	private void checkNotRunning() throws BrewControllerException {
		if (state.getOperationMode() == MODE.MASHING) {
			throw new BrewControllerStateException(
					"The mash program can not be modified if mashing is running");
		}
		if (hardware == null) {
			throw new IllegalStateException("No hardware interface available");
		}
	}

	private void serializeState() {
		File path = new File(serializationPath);
		if (!path.exists()) {
			try {
				FileUtils.forceMkdir(path);
			} catch (IOException e) {
				log.error("Can't create path for serialization of state", e);
			}
		}
		File stateFile = new File(path, generateFileName());
		File oldStateFile = new File(path, generateFileName() + ".old");
		try {
			if (oldStateFile.exists()) {
				FileUtils.forceDelete(oldStateFile);
			}
			if (stateFile.exists()) {
				FileUtils.copyFile(stateFile, oldStateFile);
				FileUtils.forceDelete(stateFile);
				stateFile.createNewFile();
			}
			jacksonMapper.writeValue(stateFile, state);
		} catch (IOException e) {
			log.error("Error while serializing current state", e);
		}
	}

	private void notifyNextStep() {
		synchronized (listeners) {
			for (BrewControllerListener l : listeners) {
				l.stepChanged(currentStep);
			}
		}
	}

	private void notifyMeasuredTemp(float temp) {
		synchronized (listeners) {
			for (BrewControllerListener l : listeners) {
				l.measuredTempChanged(temp);
			}
		}
	}

	private void notifyError(Exception e) {
		synchronized (listeners) {
			for (BrewControllerListener l : listeners) {
				l.error(e);
			}
		}
	}

	private String generateFileName() {
		return "brew_controller.state";
	}

	private void checkSerialization() {
		if (System.currentTimeMillis() + serializationInterval > lastStateSerialization) {
			serializeState();
			lastStateSerialization = System.currentTimeMillis();
		}
	}

	public void loadSavedState() throws BrewControllerException {
		checkNotRunning();
		File path = new File(serializationPath);
		File stateFile = new File(path, generateFileName());
		try {
			state = jacksonMapper.readValue(stateFile,
					BrewControllerState.class);
		} catch (Exception e) {
			log.error("Can't load old state", e);
		}
	}

	public void heatToTemperature(float temp) throws BrewControllerException {
		checkNotRunning();
		hardware.setTargetTemperature(temp);
	}

	public void setBrewHardware(BrewHardwareInterface hardware) {
		this.hardware = hardware;
		this.hardware.registerListener(this);
	}

	private class ProcessThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				InternalProcessStep step = getNextMashStep();
				System.out.println("Loading next step");
				if (step != null) {
					notifyNextStep();
					step.setHardware(hardware);
					log.info("Starting next Step. StepType: "
							+ step.getProcessStep().getStepType().toString());
					step.start();
					serializeState();
					while (!step.isFinished()) {
						state.getTempLog().addTempPoint(state.getTimeRunning(),
								hardware.getCurrentTemperature());
						checkSerialization();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							log.error(
									"Exception while waiting for process step",
									e);
						}
					}
				} else {
					log.info("Last step was null, so I gues we ware finished");
					resetBrewController();
					notifyNextStep();
					interrupt();
				}
				serializeState();
			}
		}
	}

	public void setMashConfiguration(BrewControllerConfiguration config)
			throws BrewControllerException {
		checkNotRunning();
		try {
			state = new BrewControllerState();
			state.setCurrentStepNumber(-1);
			List<InternalProcessStep> processSteps = new ArrayList<InternalProcessStep>(
					config.getProcessSteps().size());
			for (ProcessStep step : config.getProcessSteps()) {
				InternalProcessStep internalStep = new InternalProcessStep(step);
				processSteps.add(internalStep);
			}
			state.setProcessSteps(processSteps);
			state.setName(config.getMashName());
		} catch (Exception e) {
			throw new BrewControllerStateException("Config is invalid");
		}

	}

	public void setFixedTemperature(float temp) throws BrewControllerException {
		state.setOperationMode(MODE.FIXED_TEMP);
		hardware.setTargetTemperature(temp);
	}

	public void acknowledgeNotificationStep() throws BrewControllerException {
		if (currentStep != null
				&& currentStep.getProcessStep().getStepType() == StepType.NOTIFICATION) {
			currentStep.acknowledge();
		} else {
			throw new BrewControllerStateException(
					"Current step can't be acknowledged");
		}

	}

	public void registerBrewControllerListener(BrewControllerListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}

	}

	public void unregisterBrewControllerListener(BrewControllerListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	public BrewControllerState getCurrentState() {
		return state;
	}

	public boolean isMashing() {
		return state.getOperationMode() == MODE.MASHING;
	}

	public void setHardware(BrewHardwareInterface hardware) {
		this.hardware = hardware;

	}

	public void startCooking() throws BrewControllerException {
		checkNotRunning();
		state.setOperationMode(MODE.COOKING);
		hardware.cook();
	}

	public void stopCooking() throws BrewControllerException {
		checkNotRunning();
		state.setOperationMode(MODE.OFF);
		hardware.heatingOff();
	}

}
