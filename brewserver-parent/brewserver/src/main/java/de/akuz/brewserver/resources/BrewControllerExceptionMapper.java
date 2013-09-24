package de.akuz.brewserver.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.akuz.brewcontroller.BrewControllerException;
import de.akuz.brewcontroller.BrewControllerStateException;

@Provider
public class BrewControllerExceptionMapper implements
		ExceptionMapper<BrewControllerException> {

	public Response toResponse(BrewControllerException exception) {
		Status status = Status.PRECONDITION_FAILED;
		if (exception instanceof BrewControllerStateException) {
			status = Status.PRECONDITION_FAILED;
		}

		Response response = Response.status(status).type(MediaType.TEXT_PLAIN)
				.entity(exception.getMessage()).build();
		return response;
	}

}
