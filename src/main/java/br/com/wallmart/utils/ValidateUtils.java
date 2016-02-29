package br.com.wallmart.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;

public class ValidateUtils {

	private ValidateUtils() {
	}

	public static void isNull(Object object, String message) {
		if (object == null) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("code", Status.BAD_REQUEST.getStatusCode());
			jsonObject.addProperty("message", message);

			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(jsonObject.toString()).build());
		}
	}
}
