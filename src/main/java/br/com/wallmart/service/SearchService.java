package br.com.wallmart.service;

import java.math.BigDecimal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.neo4j.rest.graphdb.entity.RestNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.wallmart.auth.Authenticator;
import br.com.wallmart.model.DistanceAndRoute;
import br.com.wallmart.utils.Config;

public class SearchService {

	private SearchService() {
	}

	public static DistanceAndRoute searchShorterRoute(RestNode origin, RestNode destiny) {
		JsonObject jsonResponse = searchRestApi(origin, destiny);

		BigDecimal distance = jsonResponse.get("weight").getAsBigDecimal();

		DistanceAndRoute distanceAndRoute = new DistanceAndRoute(distance);

		JsonArray jsonArray = jsonResponse.get("nodes").getAsJsonArray();
		jsonArray.forEach(action -> {
			distanceAndRoute.addRouteId(Long.parseLong(getLastBitFromUrl(action.getAsString())));
		});

		return distanceAndRoute;
	}

	private static JsonObject searchRestApi(RestNode origin, RestNode destiny) {
		Client client = ClientBuilder.newClient();
		client.register(
				new Authenticator(Config.NEO4J.getString("neo4j.username"), Config.NEO4J.getString("neo4j.password")));
		WebTarget webTarget = client.target(origin.getUri()).path("path");

		String payload = createPayload(destiny);
		String jsonResponse = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(payload), String.class);

		return (JsonObject) new JsonParser().parse(jsonResponse);
	}

	private static String createPayload(RestNode destiny) {
		JsonObject relationships = new JsonObject();
		relationships.addProperty("type", RouteRelationshipType.ROUTE.toString());
		relationships.addProperty("direction", "out");

		JsonObject obj = new JsonObject();
		obj.addProperty("to", destiny.getUri());
		obj.addProperty("cost_property", "distance");
		obj.add("relationships", relationships);
		obj.addProperty("algorithm", "dijkstra");

		return obj.toString();
	}

	private static String getLastBitFromUrl(final String url) {
		return url.replaceFirst(".*/([^/?]+).*", "$1");
	}
}
