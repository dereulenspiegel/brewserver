package de.akuz.brewserver.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class BrewServerConfiguration extends Configuration {

	private HardwareConfiguration hardwareConfig;

	private NotificationConfiguration notificationConfig;

	@NotEmpty
	@JsonProperty
	private String pathToState;

	public String getPathToState() {
		return pathToState;
	}

	public void setPathToState(String pathToState) {
		this.pathToState = pathToState;
	}

	public HardwareConfiguration getHardwareConfig() {
		return hardwareConfig;
	}

	public void setHardwareConfig(HardwareConfiguration hardwareConfig) {
		this.hardwareConfig = hardwareConfig;
	}

	public NotificationConfiguration getNotificationConfig() {
		return notificationConfig;
	}

	public void setNotificationConfig(
			NotificationConfiguration notificationConfiguration) {
		this.notificationConfig = notificationConfiguration;
	}

}
