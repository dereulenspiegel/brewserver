package de.akuz.brewserver.objects;

public class Notification {

	private long timeSend;
	private String message;

	public long getTimeSend() {
		return timeSend;
	}

	public void setTimeSend(long timeSend) {
		this.timeSend = timeSend;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
