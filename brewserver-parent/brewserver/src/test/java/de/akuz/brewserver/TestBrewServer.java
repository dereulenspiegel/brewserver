package de.akuz.brewserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.akuz.brewcontroller.BrewController;
import de.akuz.brewcontroller.BrewControllerState;
import de.akuz.brewcontroller.BrewControllerStateException;
import de.akuz.brewserver.mock.TestHardware;
import de.akuz.brewserver.objects.BrewControllerConfiguration;
import de.akuz.brewserver.objects.ProcessStep;
import de.akuz.brewserver.objects.ProcessStep.StepType;
import de.akuz.brewserver.objects.internal.InternalProcessStep;
import de.akuz.brewserver.resources.BrewServerSetProcessConfiguration;
import de.akuz.brewserver.resources.BrewServerStartResource;

public class TestBrewServer {

	private BrewController controller = BrewController.getInstance();

	@Before
	public void before() throws Exception {
		controller.setSerialiaztionPath("./");
		controller.getCurrentState().setProcessSteps(null);
		controller.setBrewHardware(new TestHardware());
		controller.stopMashing();
	}

	@Test
	public void tetsSetInvalidConfiguration() throws Exception {
		BrewServerSetProcessConfiguration addStepResource = new BrewServerSetProcessConfiguration();

		BrewControllerConfiguration config = new BrewControllerConfiguration();

		try {
			addStepResource.setConfiguration(config);
			fail();
		} catch (BrewControllerStateException e) {
			assertEquals("Config is invalid", e.getMessage());
		}
	}

	@Test
	public void testSetValidConfiguration() throws Exception {
		BrewServerSetProcessConfiguration addStepResource = new BrewServerSetProcessConfiguration();

		BrewControllerConfiguration config = new BrewControllerConfiguration();
		ProcessStep step1 = new ProcessStep();
		step1.setStepType(StepType.MASH);
		step1.setName("Aufheizen");
		step1.setTargetTemp(54.0f);
		step1.setStepTime(1000);

		ProcessStep step2 = new ProcessStep();
		step2.setStepType(StepType.NOTIFICATION);
		step2.setMessage("Malz rein");

		ProcessStep step3 = new ProcessStep();
		step3.setStepType(StepType.MASH);
		step3.setName("Kombirast");
		step3.setStepTime(9000);
		step3.setTargetTemp(68.0f);
		config.addProcessStep(step1);
		config.addProcessStep(step2);
		config.addProcessStep(step3);

		addStepResource.setConfiguration(config);

		BrewControllerState state = controller.getCurrentState();

		assertEquals(3, state.getProcessSteps().size());

		InternalProcessStep internalStep1 = state.getProcessSteps().get(0);
		assertMashStep(internalStep1, 1000, "Aufheizen", 54.0f);
		InternalProcessStep internalStep2 = state.getProcessSteps().get(1);
		assertNotificationStep(internalStep2, "Malz rein");
		InternalProcessStep internalStep3 = state.getProcessSteps().get(2);
		assertMashStep(internalStep3, 9000, "Kombirast", 68.0f);
	}

	@Test
	public void testStartWithMultipleSteps() throws Exception {
		testSetValidConfiguration();

		BrewServerStartResource startResource = new BrewServerStartResource();
		startResource.startMash();
	}

	@Test
	public void testStartWithoutSteps() throws Exception {
		BrewServerStartResource startResource = new BrewServerStartResource();

		try {
			startResource.startMash();
			fail();
		} catch (BrewControllerStateException e) {
			assertEquals("No Process steps are defined", e.getMessage());
		}
	}

	private void assertNotificationStep(InternalProcessStep step,
			String expectedMessage) {
		assertEquals(StepType.NOTIFICATION, step.getProcessStep().getStepType());
		assertEquals(expectedMessage, step.getMessage());
	}

	private void assertMashStep(InternalProcessStep step,
			long expectedStepTime, String expectedName, float expectedTargetTemp) {
		assertEquals(StepType.MASH, step.getProcessStep().getStepType());
		assertEquals(expectedStepTime, step.getStepTime());
		assertEquals(expectedName, step.getName());
		assertEquals(expectedTargetTemp, step.getTargetTemp(), 0.1);
	}

}
