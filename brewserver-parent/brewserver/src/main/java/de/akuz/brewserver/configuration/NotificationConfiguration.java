package de.akuz.brewserver.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationConfiguration {

	@JsonProperty
	private boolean notifyViaUDP;

	@JsonProperty
	private String customNotifcationImpl;

	@JsonProperty
	private String customNotificationOptions;

	public boolean isNotifyViaUDP() {
		return notifyViaUDP;
	}

	public void setNotifyViaUDP(boolean notifyViaUDP) {
		this.notifyViaUDP = notifyViaUDP;
	}

	public String getCustomNotifcationImpl() {
		return customNotifcationImpl;
	}

	public void setCustomNotifcationImpl(String customNotifcationImpl) {
		this.customNotifcationImpl = customNotifcationImpl;
	}

	public String getCustomNotificationOptions() {
		return customNotificationOptions;
	}

	public void setCustomNotificationOptions(String customNotificationOptions) {
		this.customNotificationOptions = customNotificationOptions;
	}

}
