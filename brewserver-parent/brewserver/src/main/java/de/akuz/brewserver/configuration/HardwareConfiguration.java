package de.akuz.brewserver.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class HardwareConfiguration extends Configuration{

	@NotEmpty
	@JsonProperty
	private String hardwareImpl;

	@JsonProperty
	private String hardwareOptions;

	public String getHardwareImpl() {
		return hardwareImpl;
	}

	public void setHardwareImpl(String hardwareImpl) {
		this.hardwareImpl = hardwareImpl;
	}

	public String getHardwareOptions() {
		return hardwareOptions;
	}

	public void setHardwareOptions(String hardwareOptions) {
		this.hardwareOptions = hardwareOptions;
	}

}
