package de.akuz.brewserver;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import de.akuz.brewserver.notification.NotificationInterface;
import de.akuz.brewserver.objects.Notification;

public class AtmosphereNotifier implements NotificationInterface {

	private Broadcaster broadcaster;

	public AtmosphereNotifier() {
		broadcaster = BroadcasterFactory.getDefault().lookup("/notify", true);
	}

	public void sendNotification(String message) {
		Notification notification = new Notification();
		notification.setTimeSend(System.currentTimeMillis());
		notification.setMessage(message);
		broadcaster.broadcast(notification);
	}

}
