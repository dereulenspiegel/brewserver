package de.akuz.brewserver.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class BrewServerConfiguration extends Configuration {

	private HardwareConfiguration hardwareConfig;

	@NotEmpty
	@JsonProperty
	private String notificationImpl;

	@NotEmpty
	@JsonProperty
	private String pathToState;

	public String getNotificationImpl() {
		return notificationImpl;
	}

	public String getPathToState() {
		return pathToState;
	}

	public void setNotificationImpl(String notificationImpl) {
		this.notificationImpl = notificationImpl;
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

}
