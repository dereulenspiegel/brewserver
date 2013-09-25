package de.akuz.brewcontroller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akuz.brewserver.notification.NotificationInterface;

public class NotificationManager {

	private final static Logger log = LoggerFactory
			.getLogger(NotificationManager.class);

	private static NotificationManager instance = new NotificationManager();

	private List<NotificationInterface> notifiers = new ArrayList<NotificationInterface>();

	private NotificationManager() {

	}

	public static NotificationManager getInstance() {
		return instance;
	}

	public void registerNotifier(NotificationInterface notifier) {
		log.debug("Registering new notifier: "
				+ notifier.getClass().getCanonicalName());
		if (notifier != null) {
			notifiers.add(notifier);
		}
	}

	public void unregisterNotifier(NotificationInterface notifier) {
		log.debug("Unregestiering notifier: "
				+ notifier.getClass().getCanonicalName());
		if (notifier != null) {
			notifiers.remove(notifier);
		}
	}

	public void notify(String message) {
		if (notifiers.size() == 0) {
			log.warn("The message will be lost, no notifier are registered. Lost message: "
					+ message);
		}
		for (NotificationInterface notifier : notifiers) {
			notifier.sendNotification(message);
		}
	}

}
