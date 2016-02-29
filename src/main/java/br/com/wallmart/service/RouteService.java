package br.com.wallmart.service;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.wallmart.model.Map;
import br.com.wallmart.model.ShorterRoute;
import br.com.wallmart.utils.Config;
import br.com.wallmart.utils.ValidateUtils;

@Path("/route")
public class RouteService {

	@GET
	@Path("/findShorterRoute")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getShoterRoute(@QueryParam("origin") String origin, @QueryParam("destiny") String destiny,
			@QueryParam("autonomy") BigDecimal autonomy, @QueryParam("fuelPrice") BigDecimal fuelPrice) {
		ValidateUtils.isNull(origin, Config.MSG.getString("msg.origin.required"));
		ValidateUtils.isNull(destiny, Config.MSG.getString("msg.destiny.required"));
		ValidateUtils.isNull(autonomy, Config.MSG.getString("msg.autonomy.required"));
		ValidateUtils.isNull(fuelPrice, Config.MSG.getString("msg.fuelPrice.required"));

		Neo4jService neo4jService = new Neo4jService();
		ShorterRoute shorterRoute = neo4jService.getShorterRoutes(origin, destiny, autonomy, fuelPrice);

		return Response.status(Status.OK).entity(shorterRoute).build();
	}

	@POST
	@Path("/createMap")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createMap(Map map) {
		ValidateUtils.isNull(map, Config.MSG.getString("msg.map.required"));

		Neo4jService neo4jService = new Neo4jService();
		neo4jService.persist(map);

		return Response.status(Status.CREATED).build();
	}

}
