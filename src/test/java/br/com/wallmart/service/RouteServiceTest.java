package br.com.wallmart.service;

import static org.hamcrest.CoreMatchers.is;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.wallmart.model.Map;
import br.com.wallmart.model.Route;

public class RouteServiceTest {

	private Map map;

	@Before
	public void setup() {
		List<Route> routes = new ArrayList<>();
		routes.add(new Route("A", "B", BigDecimal.TEN));
		routes.add(new Route("B", "D", new BigDecimal(15)));
		routes.add(new Route("A", "C", new BigDecimal(20)));
		routes.add(new Route("C", "D", new BigDecimal(30)));
		routes.add(new Route("B", "E", new BigDecimal(50)));
		routes.add(new Route("D", "E", new BigDecimal(30)));

		this.map = new Map("SP", routes);
	}

	@Test(expected = WebApplicationException.class)
	public void testRequestMapIsRequired() {
		RouteService rs = new RouteService();

		rs.createMap(null);
	}

	@Test
	public void testPersistMap() {
		RouteService rs = new RouteService();
		Response response = rs.createMap(map);
		Assert.assertThat(201, is(response.getStatus()));
	}

	@Test(expected = WebApplicationException.class)
	public void testRequestShorterRouteOriginRequired() {
		RouteService rs = new RouteService();
		rs.getShoterRoute(null, "E", BigDecimal.TEN, new BigDecimal(4.5));
	}

	@Test(expected = WebApplicationException.class)
	public void testRequestShorterRouteDestinyRequired() {
		RouteService rs = new RouteService();
		rs.getShoterRoute("A", null, BigDecimal.TEN, new BigDecimal(4.5));
	}

	@Test(expected = WebApplicationException.class)
	public void testRequestShorterRouteAutonomyRequired() {
		RouteService rs = new RouteService();
		rs.getShoterRoute("A", "E", null, new BigDecimal(4.5));
	}

	@Test(expected = WebApplicationException.class)
	public void testRequestShorterRouteFuelPriceRequired() {
		RouteService rs = new RouteService();
		rs.getShoterRoute("A", "E", BigDecimal.TEN, null);
	}

}
