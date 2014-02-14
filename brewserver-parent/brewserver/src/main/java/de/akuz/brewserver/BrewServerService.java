package de.akuz.brewserver;

import java.lang.reflect.Constructor;

import org.atmosphere.cpr.AtmosphereServlet;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.FilterBuilder;

import de.akuz.brewcontroller.BrewController;
import de.akuz.brewcontroller.NotificationManager;
import de.akuz.brewserver.configuration.BrewServerConfiguration;
import de.akuz.brewserver.hardware.BrewHardwareInterface;
import de.akuz.brewserver.resources.BrewControllerExceptionMapper;
import de.akuz.brewserver.resources.BrewServerSetProcessConfiguration;
import de.akuz.brewserver.resources.BrewServerSetTempResource;
import de.akuz.brewserver.resources.BrewServerStartResource;
import de.akuz.brewserver.resources.BrewServerStopResource;
import de.akuz.brewserver.resources.CookingResource;
import de.akuz.brewserver.resources.FullStateResource;

public class BrewServerService extends Service<BrewServerConfiguration> {

	private final static Logger log = LoggerFactory
			.getLogger(BrewServerService.class);

	public static void main(String[] args) throws Exception {
		new BrewServerService().run(args);
	}

	@Override
	public void initialize(Bootstrap<BrewServerConfiguration> bootstrap) {
		bootstrap.setName("brew-server");
		// Move api to suburl in config
		bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
	}

	@Override
	public void run(final BrewServerConfiguration configuration,
			Environment environment) throws Exception {
		initializeAtmosphere(configuration, environment);
		environment.addResource(BrewServerSetProcessConfiguration.class);
		environment.addResource(BrewServerStartResource.class);
		environment.addResource(BrewServerStopResource.class);
		environment.addResource(FullStateResource.class);
		environment.addResource(CookingResource.class);
		environment.addResource(BrewServerSetTempResource.class);
		environment.addHealthCheck(new BrewServerHealthCheck());
		environment.addProvider(BrewControllerExceptionMapper.class);
		environment.addLifeCycleListener(new Listener() {

			public void lifeCycleStopping(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			public void lifeCycleStopped(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			public void lifeCycleStarting(LifeCycle arg0) {
				// TODO Auto-generated method stub

			}

			public void lifeCycleStarted(LifeCycle arg0) {
				configureBrewController(configuration);
				configureAtmosphereNotifier();

			}

			public void lifeCycleFailure(LifeCycle arg0, Throwable arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void configureAtmosphereNotifier() {
		AtmosphereNotifier notifier = new AtmosphereNotifier();
		NotificationManager.getInstance().registerNotifier(notifier);
	}

	private void configureBrewController(BrewServerConfiguration configuration) {
		BrewController controller = BrewController.getInstance();
		controller.setSerialiaztionPath(configuration.getPathToState());
		controller
				.registerBrewControllerListener(new BrewServerAtmosphereListener());
		BrewHardwareInterface hardware = loadBrewHardwareImplementation(
				configuration.getHardwareConfig().getHardwareImpl(),
				configuration.getHardwareConfig().getHardwareOptions());
		controller.setBrewHardware(hardware);
	}

	private void initializeAtmosphere(BrewServerConfiguration configuration,
			Environment environment) {
		String atmosphereUrl = parseRootUrl(configuration
				.getHttpConfiguration().getRootPath()) + "status/";
		FilterBuilder fconfig = environment.addFilter(CrossOriginFilter.class,
				atmosphereUrl + "*");
		fconfig.setInitParam(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");

		AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
		atmosphereServlet.framework().addInitParameter(
				"com.sun.jersey.config.property.packages",
				"de.akuz.brewserver.resources.atmosphere");
		atmosphereServlet.framework().addInitParameter(
				"org.atmosphere.websocket.messageContentType",
				"application/json");
		atmosphereServlet.framework().addInitParameter(
				"org.atmosphere.useWebSocket", "true");
		atmosphereServlet.framework().shareExecutorServices(true);
		// atmosphereServlet.framework().addInitParameter(
		// "org.atmosphere.cpr.broadcastFilterClasses",
		// "com.example.helloworld.filters.BadWordFilter");
		environment.addServlet(atmosphereServlet, atmosphereUrl + "*");
	}

	private String parseRootUrl(String rootPath) {
		return rootPath.substring(0, rootPath.length() - 1);
	}

	private BrewHardwareInterface loadBrewHardwareImplementation(
			String className, String optionString) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends BrewHardwareInterface> brewHardwareClass = (Class<? extends BrewHardwareInterface>) Class
					.forName(className);
			Constructor<? extends BrewHardwareInterface> brewHardwareConstructor = brewHardwareClass
					.getConstructor(new Class[] {});
			BrewHardwareInterface hardware = brewHardwareConstructor
					.newInstance(new Object[] {});
			hardware.setOptions(optionString);
			hardware.init();
			hardware.heatingOff();
			return hardware;
		} catch (Exception e) {
			log.error("Can't instantiate BrewHardwareInterface", e);
			throw new IllegalArgumentException(
					"Can't instantiate BrewHardwareInterface", e);
		}
	}

}
