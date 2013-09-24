package de.akuz.brewserver;

import com.yammer.metrics.core.HealthCheck;

public class BrewServerHealthCheck extends HealthCheck {

	protected BrewServerHealthCheck() {
		super("brew-server");
	}

	@Override
	protected Result check() throws Exception {
		// TODO Auto-generated method stub
		return Result.healthy();
	}

}
