package de.akuz.brewserver.notification;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import de.akuz.brewserver.objects.Notification;

public class AtmosphereNotification implements NotificationInterface {

	private Broadcaster broadcaster;

	public AtmosphereNotification() {
		broadcaster = BroadcasterFactory.getDefault().lookup(
				"/control/state/notification", true);
	}

	public void sendNotification(String message) {
		Notification notification = new Notification();
		notification.setMessage(message);
		notification.setTimeSend(System.currentTimeMillis());
		broadcaster.broadcast(notification);
	}
}
